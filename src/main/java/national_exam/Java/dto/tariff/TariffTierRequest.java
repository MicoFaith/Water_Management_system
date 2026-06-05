package national_exam.Java.dto.tariff;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class TariffTierRequest {

	@NotNull
	private BigDecimal minConsumption;

	private BigDecimal maxConsumption;

	@NotNull
	private BigDecimal ratePerUnit;
}
