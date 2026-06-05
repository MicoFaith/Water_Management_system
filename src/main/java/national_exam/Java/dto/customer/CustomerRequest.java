package national_exam.Java.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import national_exam.Java.enums.AccountStatus;

@Data
public class CustomerRequest {

	@NotBlank
	private String fullNames;

	@NotBlank
	private String nationalId;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String phoneNumber;

	@NotBlank
	private String address;

	private AccountStatus status;
}
