package national_exam.Java.service;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.auth.AuthResponse;
import national_exam.Java.dto.auth.LoginRequest;
import national_exam.Java.dto.auth.SignupRequest;
import national_exam.Java.dto.user.AdminUserRequest;
import national_exam.Java.entity.Customer;
import national_exam.Java.entity.Role;
import national_exam.Java.entity.User;
import national_exam.Java.enums.AccountStatus;
import national_exam.Java.enums.RoleName;
import national_exam.Java.exception.BusinessException;
import national_exam.Java.repository.CustomerRepository;
import national_exam.Java.repository.RoleRepository;
import national_exam.Java.repository.UserRepository;
import national_exam.Java.security.JwtService;
import national_exam.Java.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final CustomerRepository customerRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	@Transactional
	public AuthResponse signup(SignupRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException("Email already registered");
		}

		RoleName roleName = RoleName.ROLE_CUSTOMER;
		Role role =
				roleRepository
						.findByName(roleName)
						.orElseThrow(() -> new BusinessException("Role not found: " + roleName));

		User user =
				User.builder()
						.fullNames(request.getFullNames())
						.email(request.getEmail())
						.phoneNumber(request.getPhoneNumber())
						.password(passwordEncoder.encode(request.getPassword()))
						.status(AccountStatus.ACTIVE)
						.build();
		user.getRoles().add(role);
		userRepository.save(user);

		if (roleName == RoleName.ROLE_CUSTOMER) {
			if (request.getNationalId() == null || request.getAddress() == null) {
				throw new BusinessException("National ID and address are required for customers");
			}
			if (customerRepository.existsByNationalId(request.getNationalId())) {
				throw new BusinessException("Customer with this National ID already exists");
			}
			Customer customer =
					Customer.builder()
							.fullNames(request.getFullNames())
							.nationalId(request.getNationalId())
							.email(request.getEmail())
							.phoneNumber(request.getPhoneNumber())
							.address(request.getAddress())
							.status(AccountStatus.ACTIVE)
							.user(user)
							.build();
			customerRepository.save(customer);
		}

		String token = jwtService.generateToken(UserPrincipal.create(user));
		return buildAuthResponse(user, token);
	}

	public AuthResponse login(LoginRequest request) {
		Authentication authentication =
				authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
		User user =
				userRepository
						.findByEmail(principal.getEmail())
						.orElseThrow(() -> new BusinessException("User not found"));

		String token = jwtService.generateToken(principal);
		return buildAuthResponse(user, token);
	}

	@Transactional
	public AuthResponse registerStaff(AdminUserRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException("Email already registered");
		}

		RoleName roleName = resolveRole(request.getRole());
		if (roleName == RoleName.ROLE_CUSTOMER) {
			throw new BusinessException("Use /api/auth/signup to register customers");
		}

		Role role =
				roleRepository
						.findByName(roleName)
						.orElseThrow(() -> new BusinessException("Role not found: " + roleName));

		User user =
				User.builder()
						.fullNames(request.getFullNames())
						.email(request.getEmail())
						.phoneNumber(request.getPhoneNumber())
						.password(passwordEncoder.encode(request.getPassword()))
						.status(
								request.getStatus() != null
										? request.getStatus()
										: AccountStatus.ACTIVE)
						.build();
		user.getRoles().add(role);
		userRepository.saveAndFlush(user);

		String token = jwtService.generateToken(UserPrincipal.create(user));
		return buildAuthResponse(user, token);
	}

	private RoleName resolveRole(String role) {
		if (role == null || role.isBlank()) {
			return RoleName.ROLE_CUSTOMER;
		}
		try {
			return RoleName.valueOf(role.toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new BusinessException("Invalid role: " + role);
		}
	}

	private AuthResponse buildAuthResponse(User user, String token) {
		Set<String> roles =
				user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet());
		return AuthResponse.builder()
				.token(token)
				.email(user.getEmail())
				.fullNames(user.getFullNames())
				.roles(roles)
				.build();
	}
}
