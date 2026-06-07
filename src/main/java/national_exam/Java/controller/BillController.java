package national_exam.Java.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.bill.BillResponse;
import national_exam.Java.service.BillService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

	private final BillService billService;

	@PostMapping("/generate/{readingId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
	@ResponseStatus(HttpStatus.CREATED)
	public BillResponse generateFromReading(@PathVariable Long readingId) {
		return billService.generateBillFromReading(readingId);
	}

	@PutMapping("/{id}/approve")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
	public BillResponse approve(@PathVariable Long id) {
		return billService.approveBill(id);
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
	public List<BillResponse> getAll() {
		return billService.getAllBills();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'CUSTOMER')")
	public BillResponse getById(@PathVariable Long id) {
		return billService.getBillById(id);
	}

	@GetMapping("/customer/{customerId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'CUSTOMER')")
	public List<BillResponse> getByCustomer(@PathVariable Long customerId) {
		return billService.getBillsByCustomer(customerId);
	}
}
