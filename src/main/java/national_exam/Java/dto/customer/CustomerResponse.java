package national_exam.Java.dto.customer;

import lombok.Builder;
import lombok.Data;
import national_exam.Java.enums.AccountStatus;

@Data
@Builder
public class CustomerResponse {

	private Long id;
	private String fullNames;
	private String nationalId;
	private String email;
	private String phoneNumber;
	private String address;
	private AccountStatus status;
}
