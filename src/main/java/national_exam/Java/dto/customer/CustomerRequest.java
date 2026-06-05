package national_exam.Java.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import national_exam.Java.enums.AccountStatus;
import national_exam.Java.validation.ValidationPatterns;

@Data
public class CustomerRequest {

	@NotBlank
	private String fullNames;

	@NotBlank
	@Pattern(regexp = ValidationPatterns.NATIONAL_ID, message = "National ID must be 16 digits")
	private String nationalId;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Pattern(regexp = ValidationPatterns.RWANDA_PHONE, message = "Invalid Rwanda phone number")
	private String phoneNumber;

	@NotBlank
	private String address;

	private AccountStatus status;
}
