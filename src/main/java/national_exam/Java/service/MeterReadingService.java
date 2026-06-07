package national_exam.Java.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import national_exam.Java.dto.reading.MeterReadingRequest;
import national_exam.Java.dto.reading.MeterReadingResponse;
import national_exam.Java.entity.Meter;
import national_exam.Java.entity.MeterReading;
import national_exam.Java.enums.AccountStatus;
import national_exam.Java.exception.BusinessException;
import national_exam.Java.exception.ResourceNotFoundException;
import national_exam.Java.repository.MeterReadingRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class MeterReadingService {

	private final MeterReadingRepository meterReadingRepository;
	private final MeterService meterService;
	private final BillService billService;

	public MeterReadingService(
			MeterReadingRepository meterReadingRepository,
			MeterService meterService,
			@Lazy BillService billService) {
		this.meterReadingRepository = meterReadingRepository;
		this.meterService = meterService;
		this.billService = billService;
	}

	@Transactional
	public MeterReadingResponse capture(MeterReadingRequest request) {
		Meter meter = meterService.findMeter(request.getMeterId());

		if (meter.getStatus() != AccountStatus.ACTIVE) {
			throw new BusinessException("Cannot capture reading for inactive meter");
		}

		if (request.getReadingDate().isAfter(LocalDate.now())) {
			throw new BusinessException("Reading date cannot be in the future");
		}

		if (request.getCurrentReading().compareTo(request.getPreviousReading()) <= 0) {
			throw new BusinessException("Current reading must be greater than previous reading");
		}

		int month = request.getReadingDate().getMonthValue();
		int year = request.getReadingDate().getYear();

		if (meterReadingRepository.existsByMeterIdAndBillingMonthAndBillingYear(
				meter.getId(), month, year)) {
			throw new BusinessException("A reading already exists for this meter in " + month + "/" + year);
		}

		MeterReading reading =
				MeterReading.builder()
						.meter(meter)
						.previousReading(request.getPreviousReading())
						.currentReading(request.getCurrentReading())
						.readingDate(request.getReadingDate())
						.billingMonth(month)
						.billingYear(year)
						.build();

		MeterReading saved = meterReadingRepository.save(reading);

		try {
			billService.generateBillFromReading(saved.getId());
		} catch (BusinessException ex) {
			log.warn("Auto bill generation skipped for reading {}: {}", saved.getId(), ex.getMessage());
		}

		return toResponse(saved);
	}

	public List<MeterReadingResponse> getAll() {
		return meterReadingRepository.findAll().stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}

	public List<MeterReadingResponse> getByMeter(Long meterId) {
		return meterReadingRepository.findByMeterId(meterId).stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}

	public MeterReadingResponse getById(Long id) {
		return toResponse(findReading(id));
	}

	public MeterReading findReading(Long id) {
		return meterReadingRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Meter reading not found with id: " + id));
	}

	private MeterReadingResponse toResponse(MeterReading reading) {
		BigDecimal consumption = reading.getCurrentReading().subtract(reading.getPreviousReading());
		return MeterReadingResponse.builder()
				.id(reading.getId())
				.meterId(reading.getMeter().getId())
				.meterNumber(reading.getMeter().getMeterNumber())
				.previousReading(reading.getPreviousReading())
				.currentReading(reading.getCurrentReading())
				.readingDate(reading.getReadingDate())
				.billingMonth(reading.getBillingMonth())
				.billingYear(reading.getBillingYear())
				.consumption(consumption)
				.build();
	}
}
