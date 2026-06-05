package national_exam.Java.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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
	@Positive(message = "Amount paid must be greater than zero")
	private BigDecimal amountPaid;

	@NotNull
	private PaymentMethod paymentMethod;

	@NotNull
	@PastOrPresent(message = "Payment date cannot be in the future")
	private LocalDate paymentDate;
}
