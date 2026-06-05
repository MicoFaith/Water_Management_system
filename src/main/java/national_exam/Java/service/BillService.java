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
import national_exam.Java.enums.NotificationType;
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
	private final NotificationService notificationService;
	private final EmailService emailService;

	@Transactional
	public BillResponse generateBillFromReading(Long readingId) {
		MeterReading reading =
				meterReadingRepository
						.findById(readingId)
						.orElseThrow(
								() -> new ResourceNotFoundException("Meter reading not found with id: " + readingId));
		Meter meter = reading.getMeter();
		Customer customer = meter.getCustomer();

		if (customer.getStatus() != AccountStatus.ACTIVE) {
			throw new BusinessException("Inactive customers cannot receive bills");
		}

		if (billRepository
				.findByMeterIdAndBillingMonthAndBillingYear(
						meter.getId(), reading.getBillingMonth(), reading.getBillingYear())
				.isPresent()) {
			throw new BusinessException("Bill already exists for this meter and billing period");
		}

		BigDecimal consumption =
				reading.getCurrentReading().subtract(reading.getPreviousReading());
		if (consumption.signum() <= 0) {
			throw new BusinessException("Consumption must be greater than zero");
		}

		return saveBill(
				customer,
				meter,
				reading,
				meter.getMeterType(),
				reading.getBillingMonth(),
				reading.getBillingYear(),
				consumption);
	}

	@Transactional
	public BillResponse generateBill(BillGenerationRequest request) {
		Customer customer = customerService.findCustomer(request.getCustomerId());

		if (customer.getStatus() != AccountStatus.ACTIVE) {
			throw new BusinessException("Inactive customers cannot receive bills");
		}

		List<Meter> meters =
				meterRepository.findByCustomerIdAndMeterType(customer.getId(), request.getMeterType());
		if (meters.isEmpty()) {
			throw new BusinessException("Customer has no " + request.getMeterType() + " meter");
		}

		Meter meter = meters.getFirst();
		MeterReading reading =
				meterReadingRepository
						.findByMeterIdAndBillingMonthAndBillingYear(
								meter.getId(), request.getBillingMonth(), request.getBillingYear())
						.orElseThrow(() -> new BusinessException("Meter reading not found for billing period"));

		return generateBillFromReading(reading.getId());
	}

	@Transactional
	public BillResponse approveBill(Long billId) {
		Bill bill = findBill(billId);
		if (bill.getStatus() != BillStatus.PENDING) {
			throw new BusinessException("Only pending bills can be approved");
		}
		if (bill.getStatus() == BillStatus.APPROVED) {
			throw new BusinessException("Bill is already approved");
		}

		bill.setStatus(BillStatus.APPROVED);
		Bill saved = billRepository.save(bill);

		notificationService.createBillNotification(
				saved.getCustomer(), saved, NotificationType.BILL_GENERATED, false);
		emailService.sendBillApprovalEmail(saved.getCustomer(), saved);

		return toResponse(saved);
	}

	public List<BillResponse> getAllBills() {
		return billRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
	}

	public List<BillResponse> getBillsByCustomer(Long customerId) {
		return billRepository.findByCustomerId(customerId).stream()
				.map(b -> toResponse(applyPenaltyIfOverdue(b)))
				.collect(Collectors.toList());
	}

	public BillResponse getBillById(Long id) {
		return toResponse(applyPenaltyIfOverdue(findBill(id)));
	}

	public Bill findBill(Long id) {
		return billRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + id));
	}

	@Transactional
	public Bill applyPenaltyIfOverdue(Bill bill) {
		if (bill.getStatus() != BillStatus.APPROVED && bill.getStatus() != BillStatus.PARTIALLY_PAID) {
			return bill;
		}
		if (!LocalDate.now().isAfter(bill.getDueDate())) {
			return bill;
		}
		if (bill.getPenaltyAmount().signum() > 0) {
			return bill;
		}

		LocalDate billingDate = YearMonth.of(bill.getBillingYear(), bill.getBillingMonth()).atEndOfMonth();
		Penalty penalty =
				penaltyRepository.findActiveForDate(billingDate).stream().findFirst().orElse(null);
		if (penalty == null) {
			return bill;
		}

		BigDecimal subtotal = bill.getConsumptionAmount().add(bill.getServiceChargeAmount());
		BigDecimal penaltyAmount =
				subtotal
						.add(bill.getTaxAmount())
						.multiply(penalty.getPercentage())
						.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

		bill.setPenaltyAmount(penaltyAmount);
		bill.setTotalAmount(subtotal.add(bill.getTaxAmount()).add(penaltyAmount));
		bill.setOutstandingBalance(bill.getTotalAmount().subtract(bill.getAmountPaid()));
		bill.setStatus(BillStatus.OVERDUE);
		return billRepository.save(bill);
	}

	private BillResponse saveBill(
			Customer customer,
			Meter meter,
			MeterReading reading,
			MeterType meterType,
			Integer billingMonth,
			Integer billingYear,
			BigDecimal consumption) {

		LocalDate billingDate = YearMonth.of(billingYear, billingMonth).atEndOfMonth();

		Tariff tariff =
				tariffRepository.findActiveTariffsForDate(meterType, billingDate).stream()
						.findFirst()
						.orElseThrow(() -> new BusinessException("No active tariff configured"));

		ServiceCharge serviceCharge =
				serviceChargeRepository.findActiveForDate(meterType, billingDate).stream()
						.findFirst()
						.orElseThrow(() -> new BusinessException("No active service charge configured"));

		Tax tax =
				taxRepository.findActiveForDate(billingDate).stream()
						.findFirst()
						.orElseThrow(() -> new BusinessException("No active tax configured"));

		BigDecimal consumptionAmount = calculateConsumptionAmount(tariff, consumption);
		BigDecimal serviceChargeAmount = serviceCharge.getAmount();
		BigDecimal subtotal = consumptionAmount.add(serviceChargeAmount);
		BigDecimal taxAmount =
				subtotal.multiply(tax.getPercentage()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
		BigDecimal totalAmount = subtotal.add(taxAmount);
		LocalDate dueDate = billingDate.plusDays(30);

		Bill bill =
				Bill.builder()
						.customer(customer)
						.meter(meter)
						.reading(reading)
						.meterType(meterType)
						.billingMonth(billingMonth)
						.billingYear(billingYear)
						.consumption(consumption)
						.consumptionAmount(consumptionAmount)
						.serviceChargeAmount(serviceChargeAmount)
						.taxAmount(taxAmount)
						.penaltyAmount(BigDecimal.ZERO)
						.totalAmount(totalAmount)
						.amountPaid(BigDecimal.ZERO)
						.outstandingBalance(totalAmount)
						.status(BillStatus.PENDING)
						.dueDate(dueDate)
						.generatedDate(LocalDate.now())
						.build();

		Bill saved = billRepository.save(bill);
		notificationService.createBillNotification(
				customer, saved, NotificationType.BILL_GENERATED, true);
		emailService.sendBillGeneratedEmail(customer, saved);
		return toResponse(saved);
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
				.consumption(bill.getConsumption())
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
