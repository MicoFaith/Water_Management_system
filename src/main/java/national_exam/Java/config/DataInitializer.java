package national_exam.Java.config;

import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import national_exam.Java.entity.Role;
import national_exam.Java.entity.User;
import national_exam.Java.enums.AccountStatus;
import national_exam.Java.enums.RoleName;
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
	private final PasswordEncoder passwordEncoder;

	@Bean
	CommandLineRunner initRolesAndAdmin() {
		return args -> seedRolesAndAdmin();
	}

	@Transactional
	void seedRolesAndAdmin() {
		for (RoleName roleName : RoleName.values()) {
			roleRepository
					.findByName(roleName)
					.orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));
		}

		if (!userRepository.existsByEmail("admin@wasac.rw")) {
			Role adminRole =
					roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow();

			User admin =
					User.builder()
							.fullNames("System Administrator")
							.email("admin@wasac.rw")
							.phoneNumber("+250780000000")
							.password(passwordEncoder.encode("admin123"))
							.status(AccountStatus.ACTIVE)
							.build();
			admin.getRoles().add(adminRole);
			userRepository.saveAndFlush(admin);
		}
	}
}
