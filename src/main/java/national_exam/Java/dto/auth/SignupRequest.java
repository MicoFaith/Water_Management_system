package national_exam.Java.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {

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

	private String nationalId;
	private String address;
	private String role;
}
