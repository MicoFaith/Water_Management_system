package national_exam.Java.service;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.auth.AuthResponse;
import national_exam.Java.dto.auth.LoginRequest;
import national_exam.Java.dto.auth.OtpResponse;
import national_exam.Java.dto.auth.RegisterRequest;
import national_exam.Java.dto.auth.VerifyOtpRequest;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
	private final OtpService otpService;

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException("Email already registered");
		}

		RoleName roleName = resolveRegistrationRole(request);
		String fullNames = request.getFirstName().trim() + " " + request.getLastName().trim();

		Role role =
				roleRepository
						.findByName(roleName)
						.orElseThrow(() -> new BusinessException("Role not found: " + roleName));

		User user =
				User.builder()
						.fullNames(fullNames)
						.email(request.getEmail())
						.phoneNumber(request.getPhoneNumber())
						.password(passwordEncoder.encode(request.getPassword()))
						.status(AccountStatus.ACTIVE)
						.build();
		user.getRoles().add(role);
		userRepository.saveAndFlush(user);

		if (roleName == RoleName.ROLE_CUSTOMER) {
			if (request.getNationalId() == null || request.getAddress() == null) {
				throw new BusinessException("National ID and address are required for customers");
			}
			if (customerRepository.existsByNationalId(request.getNationalId())) {
				throw new BusinessException("Customer with this National ID already exists");
			}
			Customer customer =
					Customer.builder()
							.fullNames(fullNames)
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

	public OtpResponse login(LoginRequest request) {
		User user =
				userRepository
						.findByEmail(request.getEmail())
						.orElseThrow(() -> new BusinessException("Invalid email or password"));

		if (user.getStatus() != AccountStatus.ACTIVE) {
			throw new BusinessException("Account is disabled");
		}

		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		otpService.generateAndSendOtp(request.getEmail());

		return OtpResponse.builder()
				.message("OTP sent successfully")
				.email(request.getEmail())
				.build();
	}

	public AuthResponse verifyOtp(VerifyOtpRequest request) {
		User user =
				userRepository
						.findByEmail(request.getEmail())
						.orElseThrow(() -> new BusinessException("User not found"));

		if (user.getStatus() != AccountStatus.ACTIVE) {
			throw new BusinessException("Account is disabled");
		}

		otpService.verifyOtp(request.getEmail(), request.getOtp());

		String token = jwtService.generateToken(UserPrincipal.create(user));
		return buildAuthResponse(user, token);
	}

	private RoleName resolveRegistrationRole(RegisterRequest request) {
		boolean isAdmin = currentUserHasRole("ROLE_ADMIN");

		if (request.getRole() == null || request.getRole().isBlank()) {
			return RoleName.ROLE_CUSTOMER;
		}

		RoleName requested = resolveRole(request.getRole());

		if (requested == RoleName.ROLE_CUSTOMER) {
			return RoleName.ROLE_CUSTOMER;
		}

		if (!isAdmin) {
			throw new AccessDeniedException("Public users cannot register staff roles");
		}

		return requested;
	}

	private boolean currentUserHasRole(String role) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			return false;
		}
		return auth.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch(role::equals);
	}

	private RoleName resolveRole(String role) {
		String normalized = role.toUpperCase();
		if (!normalized.startsWith("ROLE_")) {
			normalized = "ROLE_" + normalized;
		}
		try {
			return RoleName.valueOf(normalized);
		} catch (IllegalArgumentException ex) {
			throw new BusinessException("Invalid role. Allowed: ADMIN, OPERATOR, FINANCE, CUSTOMER");
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
