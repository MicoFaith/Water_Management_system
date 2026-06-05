package national_exam.Java.dto.meter;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import national_exam.Java.enums.AccountStatus;
import national_exam.Java.enums.MeterType;

@Data
@Builder
public class MeterResponse {

	private Long id;
	private String meterNumber;
	private MeterType meterType;
	private LocalDate installationDate;
	private AccountStatus status;
	private Long customerId;
	private String customerName;
}
