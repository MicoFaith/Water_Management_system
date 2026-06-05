package national_exam.Java.dto.bill;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import national_exam.Java.enums.BillStatus;
import national_exam.Java.enums.MeterType;

@Data
@Builder
public class BillResponse {

	private Long id;
	private Long customerId;
	private String customerName;
	private MeterType meterType;
	private Integer billingMonth;
	private Integer billingYear;
	private BigDecimal consumption;
	private BigDecimal consumptionAmount;
	private BigDecimal serviceChargeAmount;
	private BigDecimal taxAmount;
	private BigDecimal penaltyAmount;
	private BigDecimal totalAmount;
	private BigDecimal amountPaid;
	private BigDecimal outstandingBalance;
	private BillStatus status;
	private LocalDate dueDate;
	private LocalDate generatedDate;
}
