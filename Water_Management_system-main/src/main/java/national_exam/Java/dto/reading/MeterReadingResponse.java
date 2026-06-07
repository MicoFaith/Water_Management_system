package national_exam.Java.dto.reading;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeterReadingResponse {

	private Long id;
	private Long meterId;
	private String meterNumber;
	private BigDecimal previousReading;
	private BigDecimal currentReading;
	private LocalDate readingDate;
	private Integer billingMonth;
	private Integer billingYear;
	private BigDecimal consumption;
}
