package national_exam.Java.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.payment.PaymentRequest;
import national_exam.Java.dto.payment.PaymentResponse;
import national_exam.Java.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments")
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'CUSTOMER')")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Record payment — Allowed roles: ADMIN, FINANCE, CUSTOMER")
	public PaymentResponse record(@Valid @RequestBody PaymentRequest request) {
		return paymentService.recordPayment(request);
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
	@Operation(summary = "List all payments — Allowed roles: ADMIN, FINANCE")
	public List<PaymentResponse> getAll() {
		return paymentService.getAllPayments();
	}

	@GetMapping("/bill/{billId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'CUSTOMER')")
	@Operation(summary = "List payments for bill — Allowed roles: ADMIN, FINANCE, CUSTOMER")
	public List<PaymentResponse> getByBill(@PathVariable Long billId) {
		return paymentService.getPaymentsByBill(billId);
	}

	@GetMapping("/customer/{customerId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'CUSTOMER')")
	@Operation(summary = "List payments for customer — Allowed roles: ADMIN, FINANCE, CUSTOMER")
	public List<PaymentResponse> getByCustomer(@PathVariable Long customerId) {
		return paymentService.getPaymentsByCustomer(customerId);
	}
}
