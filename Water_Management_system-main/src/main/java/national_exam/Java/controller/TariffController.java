package national_exam.Java.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.tariff.PenaltyRequest;
import national_exam.Java.dto.tariff.ServiceChargeRequest;
import national_exam.Java.dto.tariff.TariffRequest;
import national_exam.Java.dto.tariff.TaxRequest;
import national_exam.Java.entity.Penalty;
import national_exam.Java.entity.ServiceCharge;
import national_exam.Java.entity.Tariff;
import national_exam.Java.entity.Tax;
import national_exam.Java.enums.MeterType;
import national_exam.Java.service.TariffConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tariffs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Tariffs (ADMIN)")
public class TariffController {

	private final TariffConfigService tariffConfigService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create tariff — Allowed roles: ADMIN")
	public Tariff createTariff(@Valid @RequestBody TariffRequest request) {
		return tariffConfigService.createTariff(request);
	}

	@GetMapping
	@Operation(summary = "List all tariffs — Allowed roles: ADMIN")
	public List<Tariff> getAllTariffs() {
		return tariffConfigService.getAllTariffs();
	}

	@GetMapping("/active/{meterType}")
	@Operation(summary = "Get active tariff by meter type — Allowed roles: ADMIN")
	public Tariff getActiveTariff(@PathVariable MeterType meterType) {
		return tariffConfigService.getActiveTariff(meterType);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update tariff — Allowed roles: ADMIN")
	public Tariff updateTariff(@PathVariable Long id, @Valid @RequestBody TariffRequest request) {
		return tariffConfigService.updateTariff(id, request);
	}

	@PostMapping("/service-charges")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create service charge — Allowed roles: ADMIN")
	public ServiceCharge createServiceCharge(@Valid @RequestBody ServiceChargeRequest request) {
		return tariffConfigService.createServiceCharge(request);
	}

	@GetMapping("/service-charges")
	@Operation(summary = "List all service charges — Allowed roles: ADMIN")
	public List<ServiceCharge> getAllServiceCharges() {
		return tariffConfigService.getAllServiceCharges();
	}

	@PostMapping("/taxes")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create tax — Allowed roles: ADMIN")
	public Tax createTax(@Valid @RequestBody TaxRequest request) {
		return tariffConfigService.createTax(request);
	}

	@GetMapping("/taxes")
	@Operation(summary = "List all taxes — Allowed roles: ADMIN")
	public List<Tax> getAllTaxes() {
		return tariffConfigService.getAllTaxes();
	}

	@PostMapping("/penalties")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create penalty — Allowed roles: ADMIN")
	public Penalty createPenalty(@Valid @RequestBody PenaltyRequest request) {
		return tariffConfigService.createPenalty(request);
	}

	@GetMapping("/penalties")
	@Operation(summary = "List all penalties — Allowed roles: ADMIN")
	public List<Penalty> getAllPenalties() {
		return tariffConfigService.getAllPenalties();
	}
}
