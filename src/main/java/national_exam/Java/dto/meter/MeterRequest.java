package national_exam.Java.dto.meter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import national_exam.Java.enums.AccountStatus;
import national_exam.Java.enums.MeterType;

@Data
public class MeterRequest {

	@NotBlank
	private String meterNumber;

	@NotNull
	private MeterType meterType;

	@NotNull
	private LocalDate installationDate;

	private AccountStatus status;

	@NotNull
	private Long customerId;
}
