package national_exam.Java.dto.reading;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class MeterReadingRequest {

	@NotNull
	private Long meterId;

	@NotNull
	private BigDecimal previousReading;

	@NotNull
	private BigDecimal currentReading;

	@NotNull
	private LocalDate readingDate;
}
