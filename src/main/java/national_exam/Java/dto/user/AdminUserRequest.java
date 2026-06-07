package national_exam.Java.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import national_exam.Java.enums.AccountStatus;

@Data
public class AdminUserRequest {

	@NotBlank
	private String fullNames;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String phoneNumber;

	@NotBlank
	@Size(min = 6)
	private String password;

	@NotBlank
	private String role;

	private AccountStatus status;
}
