package national_exam.Java.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import national_exam.Java.enums.PaymentMethod;

@Data
public class PaymentRequest {

	@NotNull
	private Long billId;

	@NotNull
	@Positive
	private BigDecimal amountPaid;

	@NotNull
	private PaymentMethod paymentMethod;

	@NotNull
	private LocalDate paymentDate;
}
