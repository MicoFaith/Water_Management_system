package national_exam.Java.dto.tariff;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class PenaltyRequest {

	@NotBlank
	private String name;

	@NotNull
	private BigDecimal percentage;

	@NotNull
	private Integer daysAfterDue;

	@NotNull
	private LocalDate effectiveFrom;
}
