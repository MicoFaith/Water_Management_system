package national_exam.Java.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.meter.MeterRequest;
import national_exam.Java.dto.meter.MeterResponse;
import national_exam.Java.entity.Customer;
import national_exam.Java.entity.Meter;
import national_exam.Java.enums.AccountStatus;
import national_exam.Java.exception.BusinessException;
import national_exam.Java.exception.ResourceNotFoundException;
import national_exam.Java.repository.MeterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeterService {

	private final MeterRepository meterRepository;
	private final CustomerService customerService;

	@Transactional
	public MeterResponse create(MeterRequest request) {
		if (meterRepository.existsByMeterNumber(request.getMeterNumber())) {
			throw new BusinessException("Meter number already exists");
		}

		Customer customer = customerService.findCustomer(request.getCustomerId());

		Meter meter =
				Meter.builder()
						.meterNumber(request.getMeterNumber())
						.meterType(request.getMeterType())
						.installationDate(request.getInstallationDate())
						.status(request.getStatus() != null ? request.getStatus() : AccountStatus.ACTIVE)
						.customer(customer)
						.build();

		return toResponse(meterRepository.save(meter));
	}

	public List<MeterResponse> getAll() {
		return meterRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
	}

	public MeterResponse getById(Long id) {
		return toResponse(findMeter(id));
	}

	public List<MeterResponse> getByCustomer(Long customerId) {
		return meterRepository.findByCustomerId(customerId).stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}

	public Meter findMeter(Long id) {
		return meterRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Meter not found with id: " + id));
	}

	private MeterResponse toResponse(Meter meter) {
		return MeterResponse.builder()
				.id(meter.getId())
				.meterNumber(meter.getMeterNumber())
				.meterType(meter.getMeterType())
				.installationDate(meter.getInstallationDate())
				.status(meter.getStatus())
				.customerId(meter.getCustomer().getId())
				.customerName(meter.getCustomer().getFullNames())
				.build();
	}
}
