package national_exam.Java.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.customer.CustomerRequest;
import national_exam.Java.dto.customer.CustomerResponse;
import national_exam.Java.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerService customerService;

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
	@ResponseStatus(HttpStatus.CREATED)
	public CustomerResponse create(@Valid @RequestBody CustomerRequest request) {
		return customerService.create(request);
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'FINANCE')")
	public List<CustomerResponse> getAll() {
		return customerService.getAll();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'FINANCE', 'CUSTOMER')")
	public CustomerResponse getById(@PathVariable Long id) {
		return customerService.getById(id);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
		return customerService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		customerService.delete(id);
	}
}
