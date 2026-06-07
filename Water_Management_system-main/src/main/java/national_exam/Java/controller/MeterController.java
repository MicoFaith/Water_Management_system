package national_exam.Java.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.meter.MeterRequest;
import national_exam.Java.dto.meter.MeterResponse;
import national_exam.Java.service.MeterService;
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
@RequestMapping("/api/meters")
@RequiredArgsConstructor
@Tag(name = "Meters")
public class MeterController {

	private final MeterService meterService;

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Register meter — Allowed roles: ADMIN, OPERATOR")
	public MeterResponse create(@Valid @RequestBody MeterRequest request) {
		return meterService.create(request);
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'FINANCE')")
	@Operation(summary = "List all meters — Allowed roles: ADMIN, OPERATOR, FINANCE")
	public List<MeterResponse> getAll() {
		return meterService.getAll();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'FINANCE', 'CUSTOMER')")
	@Operation(summary = "Get meter by ID — Allowed roles: ADMIN, OPERATOR, FINANCE, CUSTOMER")
	public MeterResponse getById(@PathVariable Long id) {
		return meterService.getById(id);
	}

	@GetMapping("/customer/{customerId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'FINANCE', 'CUSTOMER')")
	@Operation(summary = "List meters for customer — Allowed roles: ADMIN, OPERATOR, FINANCE, CUSTOMER")
	public List<MeterResponse> getByCustomer(@PathVariable Long customerId) {
		return meterService.getByCustomer(customerId);
	}
}
