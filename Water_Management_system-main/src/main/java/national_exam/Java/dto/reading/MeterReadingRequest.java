package national_exam.Java.dto.reading;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class MeterReadingRequest {

	@NotNull
	private Long meterId;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = true)
	private BigDecimal previousReading;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false)
	private BigDecimal currentReading;

	@NotNull
	@PastOrPresent(message = "Reading date cannot be in the future")
	private LocalDate readingDate;
}
