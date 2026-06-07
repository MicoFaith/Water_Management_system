package national_exam.Java.dto.user;

import java.util.Set;
import lombok.Builder;
import lombok.Data;
import national_exam.Java.enums.AccountStatus;

@Data
@Builder
public class UserResponse {

	private Long id;
	private String fullNames;
	private String email;
	private String phoneNumber;
	private AccountStatus status;
	private Set<String> roles;
}
