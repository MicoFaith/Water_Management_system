package national_exam.Java.security;

import lombok.RequiredArgsConstructor;
import national_exam.Java.entity.User;
import national_exam.Java.enums.AccountStatus;
import national_exam.Java.exception.BusinessException;
import national_exam.Java.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user =
				userRepository
						.findByEmail(email)
						.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

		if (user.getStatus() == AccountStatus.INACTIVE) {
			throw new BusinessException("User account is inactive");
		}

		return UserPrincipal.create(user);
	}
}
