package national_exam.Java.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import national_exam.Java.enums.MeterType;
import national_exam.Java.exception.ResourceNotFoundException;
import national_exam.Java.dto.tariff.PenaltyRequest;
import national_exam.Java.dto.tariff.ServiceChargeRequest;
import national_exam.Java.dto.tariff.TariffRequest;
import national_exam.Java.dto.tariff.TariffTierRequest;
import national_exam.Java.dto.tariff.TaxRequest;
import national_exam.Java.entity.Penalty;
import national_exam.Java.entity.ServiceCharge;
import national_exam.Java.entity.Tariff;
import national_exam.Java.entity.TariffTier;
import national_exam.Java.entity.Tax;
import national_exam.Java.enums.TariffType;
import national_exam.Java.exception.BusinessException;
import national_exam.Java.repository.PenaltyRepository;
import national_exam.Java.repository.ServiceChargeRepository;
import national_exam.Java.repository.TariffRepository;
import national_exam.Java.repository.TaxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TariffConfigService {

	private final TariffRepository tariffRepository;
	private final ServiceChargeRepository serviceChargeRepository;
	private final TaxRepository taxRepository;
	private final PenaltyRepository penaltyRepository;

	@Transactional
	public Tariff createTariff(TariffRequest request) {
		validateTariffRequest(request);

		List<Tariff> existing = tariffRepository.findByMeterTypeOrderByVersionDesc(request.getMeterType());
		int nextVersion = existing.isEmpty() ? 1 : existing.getFirst().getVersion() + 1;

		existing.stream()
				.filter(t -> t.isActive() && t.getEffectiveTo() == null)
				.forEach(
						t -> {
							t.setEffectiveTo(request.getEffectiveFrom().minusDays(1));
							tariffRepository.save(t);
						});

		Tariff tariff =
				Tariff.builder()
						.name(request.getName())
						.meterType(request.getMeterType())
						.tariffType(request.getTariffType())
						.flatRate(request.getFlatRate())
						.version(nextVersion)
						.effectiveFrom(request.getEffectiveFrom())
						.active(true)
						.build();

		if (request.getTariffType() == TariffType.TIERED && request.getTiers() != null) {
			for (TariffTierRequest tierRequest : request.getTiers()) {
				TariffTier tier =
						TariffTier.builder()
								.tariff(tariff)
								.minConsumption(tierRequest.getMinConsumption())
								.maxConsumption(tierRequest.getMaxConsumption())
								.ratePerUnit(tierRequest.getRatePerUnit())
								.build();
				tariff.getTiers().add(tier);
			}
		}

		return tariffRepository.save(tariff);
	}

	@Transactional
	public ServiceCharge createServiceCharge(ServiceChargeRequest request) {
		int nextVersion =
				serviceChargeRepository.findAll().stream()
						.filter(s -> s.getMeterType() == request.getMeterType())
						.mapToInt(ServiceCharge::getVersion)
						.max()
						.orElse(0)
						+ 1;

		ServiceCharge charge =
				ServiceCharge.builder()
						.name(request.getName())
						.meterType(request.getMeterType())
						.amount(request.getAmount())
						.version(nextVersion)
						.effectiveFrom(request.getEffectiveFrom())
						.active(true)
						.build();

		return serviceChargeRepository.save(charge);
	}

	@Transactional
	public Tax createTax(TaxRequest request) {
		int nextVersion =
				taxRepository.findAll().stream().mapToInt(Tax::getVersion).max().orElse(0) + 1;

		Tax tax =
				Tax.builder()
						.name(request.getName())
						.percentage(request.getPercentage())
						.version(nextVersion)
						.effectiveFrom(request.getEffectiveFrom())
						.active(true)
						.build();

		return taxRepository.save(tax);
	}

	@Transactional
	public Penalty createPenalty(PenaltyRequest request) {
		int nextVersion =
				penaltyRepository.findAll().stream().mapToInt(Penalty::getVersion).max().orElse(0) + 1;

		Penalty penalty =
				Penalty.builder()
						.name(request.getName())
						.percentage(request.getPercentage())
						.daysAfterDue(request.getDaysAfterDue())
						.version(nextVersion)
						.effectiveFrom(request.getEffectiveFrom())
						.active(true)
						.build();

		return penaltyRepository.save(penalty);
	}

	public Tariff getActiveTariff(MeterType meterType) {
		return tariffRepository.findActiveTariffsForDate(meterType, LocalDate.now()).stream()
				.findFirst()
				.orElseThrow(() -> new BusinessException("No active tariff for " + meterType));
	}

	@Transactional
	public Tariff updateTariff(Long id, TariffRequest request) {
		Tariff existing =
				tariffRepository
						.findById(id)
						.orElseThrow(() -> new ResourceNotFoundException("Tariff not found with id: " + id));
		existing.setActive(false);
		existing.setEffectiveTo(LocalDate.now().minusDays(1));
		tariffRepository.save(existing);
		return createTariff(request);
	}

	public List<Tariff> getAllTariffs() {
		return tariffRepository.findAll();
	}

	public List<ServiceCharge> getAllServiceCharges() {
		return serviceChargeRepository.findAll();
	}

	public List<Tax> getAllTaxes() {
		return taxRepository.findAll();
	}

	public List<Penalty> getAllPenalties() {
		return penaltyRepository.findAll();
	}

	private void validateTariffRequest(TariffRequest request) {
		if (request.getTariffType() == TariffType.FLAT
				&& (request.getFlatRate() == null || request.getFlatRate().signum() <= 0)) {
			throw new BusinessException("Flat rate is required for FLAT tariff type");
		}
		if (request.getTariffType() == TariffType.TIERED
				&& (request.getTiers() == null || request.getTiers().isEmpty())) {
			throw new BusinessException("At least one tier is required for TIERED tariff type");
		}
	}
}
