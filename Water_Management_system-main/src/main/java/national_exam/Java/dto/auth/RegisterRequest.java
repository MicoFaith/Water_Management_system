package national_exam.Java.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import national_exam.Java.validation.ValidationPatterns;

@Data
public class RegisterRequest {

	@NotBlank(message = "First name is required")
	private String firstName;

	@NotBlank(message = "Last name is required")
	private String lastName;

	@NotBlank
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank
	@Pattern(regexp = ValidationPatterns.RWANDA_PHONE, message = "Phone must be a valid Rwanda number (07xxxxxxxx)")
	private String phoneNumber;

	@NotBlank
	@Size(min = 6, message = "Password must be at least 6 characters")
	private String password;

	private String role;

	@Pattern(regexp = ValidationPatterns.NATIONAL_ID, message = "National ID must be 16 digits")
	private String nationalId;

	private String address;
}
