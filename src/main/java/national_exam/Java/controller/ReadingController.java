package national_exam.Java.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.reading.MeterReadingRequest;
import national_exam.Java.dto.reading.MeterReadingResponse;
import national_exam.Java.service.MeterReadingService;
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
@RequestMapping("/api/readings")
@RequiredArgsConstructor
public class ReadingController {

	private final MeterReadingService meterReadingService;

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
	@ResponseStatus(HttpStatus.CREATED)
	public MeterReadingResponse capture(@Valid @RequestBody MeterReadingRequest request) {
		return meterReadingService.capture(request);
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'FINANCE')")
	public List<MeterReadingResponse> getAll() {
		return meterReadingService.getAll();
	}

	@GetMapping("/meter/{meterId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'FINANCE')")
	public List<MeterReadingResponse> getByMeter(@PathVariable Long meterId) {
		return meterReadingService.getByMeter(meterId);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'FINANCE')")
	public MeterReadingResponse getById(@PathVariable Long id) {
		return meterReadingService.getById(id);
	}
}
