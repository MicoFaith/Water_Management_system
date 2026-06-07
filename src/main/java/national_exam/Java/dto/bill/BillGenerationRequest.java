package national_exam.Java.dto.bill;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import national_exam.Java.enums.MeterType;

@Data
public class BillGenerationRequest {

	@NotNull
	private Long customerId;

	@NotNull
	private MeterType meterType;

	@NotNull
	@Min(1)
	@Max(12)
	private Integer billingMonth;

	@NotNull
	@Min(2000)
	private Integer billingYear;
}
