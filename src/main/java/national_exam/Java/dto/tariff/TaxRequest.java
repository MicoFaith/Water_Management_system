package national_exam.Java.dto.tariff;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class TaxRequest {

	@NotBlank
	private String name;

	@NotNull
	@Positive(message = "Percentage must be greater than zero")
	@DecimalMax(value = "100", message = "Percentage cannot exceed 100")
	private BigDecimal percentage;

	@NotNull
	private LocalDate effectiveFrom;
}
