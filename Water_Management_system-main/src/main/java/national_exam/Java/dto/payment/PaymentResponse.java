package national_exam.Java.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import national_exam.Java.enums.PaymentMethod;

@Data
@Builder
public class PaymentResponse {

	private Long id;
	private Long billId;
	private BigDecimal amountPaid;
	private PaymentMethod paymentMethod;
	private LocalDate paymentDate;
	private BigDecimal remainingBalance;
	private String billStatus;
}
