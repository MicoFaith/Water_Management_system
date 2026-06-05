package national_exam.Java.dto.tariff;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import national_exam.Java.enums.MeterType;

@Data
public class ServiceChargeRequest {

	@NotBlank
	private String name;

	@NotNull
	private MeterType meterType;

	@NotNull
	private BigDecimal amount;

	@NotNull
	private LocalDate effectiveFrom;
}
