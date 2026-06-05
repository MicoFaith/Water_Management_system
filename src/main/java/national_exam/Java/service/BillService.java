package national_exam.Java.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.bill.BillGenerationRequest;
import national_exam.Java.dto.bill.BillResponse;
import national_exam.Java.entity.Bill;
import national_exam.Java.entity.Customer;
import national_exam.Java.entity.Meter;
import national_exam.Java.entity.MeterReading;
import national_exam.Java.entity.Penalty;
import national_exam.Java.entity.ServiceCharge;
import national_exam.Java.entity.Tariff;
import national_exam.Java.entity.TariffTier;
import national_exam.Java.entity.Tax;
import national_exam.Java.enums.AccountStatus;
import national_exam.Java.enums.BillStatus;
import national_exam.Java.enums.MeterType;
import national_exam.Java.exception.BusinessException;
import national_exam.Java.exception.ResourceNotFoundException;
import national_exam.Java.repository.BillRepository;
import national_exam.Java.repository.MeterReadingRepository;
import national_exam.Java.repository.MeterRepository;
import national_exam.Java.repository.PenaltyRepository;
import national_exam.Java.repository.ServiceChargeRepository;
import national_exam.Java.repository.TariffRepository;
import national_exam.Java.repository.TaxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BillService {

	private final BillRepository billRepository;
	private final CustomerService customerService;
	private final MeterRepository meterRepository;
	private final MeterReadingRepository meterReadingRepository;
	private final TariffRepository tariffRepository;
	private final ServiceChargeRepository serviceChargeRepository;
	private final TaxRepository taxRepository;
	private final PenaltyRepository penaltyRepository;

	@Transactional
	public BillResponse generateBill(BillGenerationRequest request) {
		Customer customer = customerService.findCustomer(request.getCustomerId());

		if (customer.getStatus() != AccountStatus.ACTIVE) {
			throw new BusinessException("Inactive customers cannot receive bills");
		}

		if (billRepository
				.findByCustomerIdAndMeterTypeAndBillingMonthAndBillingYear(
						customer.getId(),
						request.getMeterType(),
						request.getBillingMonth(),
						request.getBillingYear())
				.isPresent()) {
			throw new BusinessException("Bill already exists for this period and meter type");
		}

		List<Meter> meters =
				meterRepository.findByCustomerIdAndMeterType(customer.getId(), request.getMeterType());
		if (meters.isEmpty()) {
			throw new BusinessException("Customer has no " + request.getMeterType() + " meter");
		}

		BigDecimal totalConsumption = BigDecimal.ZERO;
		for (Meter meter : meters) {
			MeterReading reading =
					meterReadingRepository
							.findByMeterIdAndBillingMonthAndBillingYear(
									meter.getId(), request.getBillingMonth(), request.getBillingYear())
							.orElseThrow(
									() ->
											new BusinessException(
													"Missing reading for meter "
															+ meter.getMeterNumber()
															+ " in "
															+ request.getBillingMonth()
															+ "/"
															+ request.getBillingYear()));
			totalConsumption =
					totalConsumption.add(reading.getCurrentReading().subtract(reading.getPreviousReading()));
		}

		LocalDate billingDate =
				YearMonth.of(request.getBillingYear(), request.getBillingMonth()).atEndOfMonth();

		Tariff tariff =
				tariffRepository
						.findActiveTariffForDate(request.getMeterType(), billingDate)
						.orElseThrow(() -> new BusinessException("No active tariff configured"));

		ServiceCharge serviceCharge =
				serviceChargeRepository
						.findActiveForDate(request.getMeterType(), billingDate)
						.orElseThrow(() -> new BusinessException("No active service charge configured"));

		Tax tax =
				taxRepository
						.findActiveForDate(billingDate)
						.orElseThrow(() -> new BusinessException("No active tax configured"));

		BigDecimal consumptionAmount = calculateConsumptionAmount(tariff, totalConsumption);
		BigDecimal serviceChargeAmount = serviceCharge.getAmount();
		BigDecimal subtotal = consumptionAmount.add(serviceChargeAmount);
		BigDecimal taxAmount =
				subtotal.multiply(tax.getPercentage()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

		BigDecimal penaltyAmount = BigDecimal.ZERO;
		LocalDate dueDate = billingDate.plusDays(30);
		if (LocalDate.now().isAfter(dueDate)) {
			Penalty penalty = penaltyRepository.findActiveForDate(billingDate).orElse(null);
			if (penalty != null
					&& LocalDate.now().isAfter(dueDate.plusDays(penalty.getDaysAfterDue()))) {
				penaltyAmount =
						subtotal
								.add(taxAmount)
								.multiply(penalty.getPercentage())
								.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
			}
		}

		BigDecimal totalAmount = subtotal.add(taxAmount).add(penaltyAmount);

		Bill bill =
				Bill.builder()
						.customer(customer)
						.meterType(request.getMeterType())
						.billingMonth(request.getBillingMonth())
						.billingYear(request.getBillingYear())
						.consumptionAmount(consumptionAmount)
						.serviceChargeAmount(serviceChargeAmount)
						.taxAmount(taxAmount)
						.penaltyAmount(penaltyAmount)
						.totalAmount(totalAmount)
						.amountPaid(BigDecimal.ZERO)
						.outstandingBalance(totalAmount)
						.status(BillStatus.PENDING_APPROVAL)
						.dueDate(dueDate)
						.generatedDate(LocalDate.now())
						.build();

		return toResponse(billRepository.save(bill));
	}

	@Transactional
	public BillResponse approveBill(Long billId) {
		Bill bill = findBill(billId);
		if (bill.getStatus() != BillStatus.PENDING_APPROVAL) {
			throw new BusinessException("Only pending bills can be approved");
		}
		bill.setStatus(BillStatus.APPROVED);
		return toResponse(billRepository.save(bill));
	}

	public List<BillResponse> getAllBills() {
		return billRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
	}

	public List<BillResponse> getBillsByCustomer(Long customerId) {
		return billRepository.findByCustomerId(customerId).stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}

	public BillResponse getBillById(Long id) {
		return toResponse(findBill(id));
	}

	public Bill findBill(Long id) {
		return billRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + id));
	}

	private BigDecimal calculateConsumptionAmount(Tariff tariff, BigDecimal consumption) {
		return switch (tariff.getTariffType()) {
			case FLAT -> consumption.multiply(tariff.getFlatRate()).setScale(2, RoundingMode.HALF_UP);
			case TIERED -> calculateTieredAmount(tariff, consumption);
		};
	}

	private BigDecimal calculateTieredAmount(Tariff tariff, BigDecimal consumption) {
		BigDecimal remaining = consumption;
		BigDecimal amount = BigDecimal.ZERO;

		List<TariffTier> tiers =
				tariff.getTiers().stream()
						.sorted(Comparator.comparing(TariffTier::getMinConsumption))
						.toList();

		for (TariffTier tier : tiers) {
			if (remaining.signum() <= 0) {
				break;
			}
			BigDecimal tierMax =
					tier.getMaxConsumption() != null
							? tier.getMaxConsumption().subtract(tier.getMinConsumption())
							: remaining;
			BigDecimal unitsInTier = remaining.min(tierMax);
			amount = amount.add(unitsInTier.multiply(tier.getRatePerUnit()));
			remaining = remaining.subtract(unitsInTier);
		}

		return amount.setScale(2, RoundingMode.HALF_UP);
	}

	private BillResponse toResponse(Bill bill) {
		return BillResponse.builder()
				.id(bill.getId())
				.customerId(bill.getCustomer().getId())
				.customerName(bill.getCustomer().getFullNames())
				.meterType(bill.getMeterType())
				.billingMonth(bill.getBillingMonth())
				.billingYear(bill.getBillingYear())
				.consumptionAmount(bill.getConsumptionAmount())
				.serviceChargeAmount(bill.getServiceChargeAmount())
				.taxAmount(bill.getTaxAmount())
				.penaltyAmount(bill.getPenaltyAmount())
				.totalAmount(bill.getTotalAmount())
				.amountPaid(bill.getAmountPaid())
				.outstandingBalance(bill.getOutstandingBalance())
				.status(bill.getStatus())
				.dueDate(bill.getDueDate())
				.generatedDate(bill.getGeneratedDate())
				.build();
	}
}
