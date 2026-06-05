package national_exam.Java.config;

import lombok.RequiredArgsConstructor;
import national_exam.Java.entity.Customer;
import national_exam.Java.entity.Role;
import national_exam.Java.entity.User;
import national_exam.Java.enums.AccountStatus;
import national_exam.Java.enums.RoleName;
import national_exam.Java.repository.CustomerRepository;
import national_exam.Java.repository.RoleRepository;
import national_exam.Java.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;
	private final PasswordEncoder passwordEncoder;

	@Bean
	CommandLineRunner initRolesAndTestUsers() {
		return args -> seedData();
	}

	@Transactional
	void seedData() {
		for (RoleName roleName : RoleName.values()) {
			roleRepository
					.findByName(roleName)
					.orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));
		}

		seedUser(
				"Faith Admin",
				"faithmico4@gmail.com",
				"0780000001",
				"Admin123",
				RoleName.ROLE_ADMIN,
				null,
				null);

		seedUser(
				"Faith Operator",
				"buterafaith@gmail.com",
				"0780000002",
				"Operator123",
				RoleName.ROLE_OPERATOR,
				null,
				null);

		seedUser(
				"Hyguette Finance",
				"isimbihyguette@gmail.com",
				"0780000003",
				"Finance123",
				RoleName.ROLE_FINANCE,
				null,
				null);

		seedUser(
				"Faith Customer",
				"faithmico25@gmail.com",
				"0780000004",
				"Customer123",
				RoleName.ROLE_CUSTOMER,
				"1199880000000001",
				"Kigali, Rwanda");
	}

	private void seedUser(
			String fullNames,
			String email,
			String phone,
			String password,
			RoleName roleName,
			String nationalId,
			String address) {

		User user = userRepository.findByEmail(email).orElse(null);

		if (user != null) {
			user.setPassword(passwordEncoder.encode(password));
			user.setStatus(AccountStatus.ACTIVE);
			userRepository.saveAndFlush(user);
		} else {
			Role role = roleRepository.findByName(roleName).orElseThrow();
			user =
					User.builder()
							.fullNames(fullNames)
							.email(email)
							.phoneNumber(phone)
							.password(passwordEncoder.encode(password))
							.status(AccountStatus.ACTIVE)
							.build();
			user.getRoles().add(role);
			userRepository.saveAndFlush(user);
		}

		if (roleName == RoleName.ROLE_CUSTOMER && nationalId != null) {
			if (customerRepository.findByUserId(user.getId()).isEmpty()
					&& !customerRepository.existsByNationalId(nationalId)) {
				Customer customer =
						Customer.builder()
								.fullNames(fullNames)
								.nationalId(nationalId)
								.email(email)
								.phoneNumber(phone)
								.address(address)
								.status(AccountStatus.ACTIVE)
								.user(user)
								.build();
				customerRepository.save(customer);
			}
		}
	}
}
