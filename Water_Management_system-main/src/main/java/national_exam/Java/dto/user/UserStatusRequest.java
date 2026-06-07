package national_exam.Java.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import national_exam.Java.enums.AccountStatus;

@Data
public class UserStatusRequest {

	@NotNull
	private AccountStatus status;
}
