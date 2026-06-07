package national_exam.Java.dto.tariff;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class TariffTierRequest {

	@NotNull
	@DecimalMin(value = "0.0", inclusive = true, message = "Minimum consumption cannot be negative")
	private BigDecimal minConsumption;

	private BigDecimal maxConsumption;

	@NotNull
	@Positive(message = "Rate per unit must be greater than zero")
	private BigDecimal ratePerUnit;
}
