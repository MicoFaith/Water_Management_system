package national_exam.Java.dto.tariff;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
	@Positive(message = "Amount must be greater than zero")
	private BigDecimal amount;

	@NotNull
	private LocalDate effectiveFrom;
}
