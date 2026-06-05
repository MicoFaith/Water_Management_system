package national_exam.Java.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.payment.PaymentRequest;
import national_exam.Java.dto.payment.PaymentResponse;
import national_exam.Java.entity.Bill;
import national_exam.Java.entity.Payment;
import national_exam.Java.enums.BillStatus;
import national_exam.Java.exception.BusinessException;
import national_exam.Java.repository.BillRepository;
import national_exam.Java.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final BillRepository billRepository;
	private final BillService billService;

	@Transactional
	public PaymentResponse recordPayment(PaymentRequest request) {
		Bill bill = billService.findBill(request.getBillId());

		if (bill.getStatus() == BillStatus.PAID) {
			throw new BusinessException("Bill is already fully paid");
		}

		if (bill.getStatus() == BillStatus.PENDING_APPROVAL) {
			throw new BusinessException("Bill must be approved before payment");
		}

		if (request.getAmountPaid().compareTo(bill.getOutstandingBalance()) > 0) {
			throw new BusinessException("Payment amount exceeds outstanding balance");
		}

		Payment payment =
				Payment.builder()
						.bill(bill)
						.amountPaid(request.getAmountPaid())
						.paymentMethod(request.getPaymentMethod())
						.paymentDate(request.getPaymentDate())
						.build();
		paymentRepository.save(payment);

		BigDecimal newAmountPaid = bill.getAmountPaid().add(request.getAmountPaid());
		BigDecimal newBalance = bill.getTotalAmount().subtract(newAmountPaid);

		bill.setAmountPaid(newAmountPaid);
		bill.setOutstandingBalance(newBalance);

		if (newBalance.signum() == 0) {
			bill.setStatus(BillStatus.PAID);
		} else {
			bill.setStatus(BillStatus.PARTIALLY_PAID);
		}
		billRepository.save(bill);

		return PaymentResponse.builder()
				.id(payment.getId())
				.billId(bill.getId())
				.amountPaid(payment.getAmountPaid())
				.paymentMethod(payment.getPaymentMethod())
				.paymentDate(payment.getPaymentDate())
				.remainingBalance(bill.getOutstandingBalance())
				.billStatus(bill.getStatus().name())
				.build();
	}

	public List<PaymentResponse> getPaymentsByBill(Long billId) {
		return paymentRepository.findByBillId(billId).stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}

	public List<PaymentResponse> getPaymentsByCustomer(Long customerId) {
		return paymentRepository.findByBillCustomerId(customerId).stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}

	private PaymentResponse toResponse(Payment payment) {
		Bill bill = payment.getBill();
		return PaymentResponse.builder()
				.id(payment.getId())
				.billId(bill.getId())
				.amountPaid(payment.getAmountPaid())
				.paymentMethod(payment.getPaymentMethod())
				.paymentDate(payment.getPaymentDate())
				.remainingBalance(bill.getOutstandingBalance())
				.billStatus(bill.getStatus().name())
				.build();
	}
}
