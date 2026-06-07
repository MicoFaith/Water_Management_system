package national_exam.Java.dto.tariff;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import national_exam.Java.enums.MeterType;
import national_exam.Java.enums.TariffType;

@Data
public class TariffRequest {

	@NotBlank
	private String name;

	@NotNull
	private MeterType meterType;

	@NotNull
	private TariffType tariffType;

	@Positive(message = "Flat rate must be greater than zero")
	private BigDecimal flatRate;

	@NotNull
	private LocalDate effectiveFrom;

	@Valid
	private List<TariffTierRequest> tiers;
}
