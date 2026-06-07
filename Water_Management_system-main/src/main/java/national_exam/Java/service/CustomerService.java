package national_exam.Java.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.customer.CustomerRequest;
import national_exam.Java.dto.customer.CustomerResponse;
import national_exam.Java.entity.Bill;
import national_exam.Java.entity.Customer;
import national_exam.Java.entity.Meter;
import national_exam.Java.entity.MeterReading;
import national_exam.Java.enums.AccountStatus;
import national_exam.Java.exception.BusinessException;
import national_exam.Java.exception.ResourceNotFoundException;
import national_exam.Java.repository.BillRepository;
import national_exam.Java.repository.CustomerRepository;
import national_exam.Java.repository.MeterReadingRepository;
import national_exam.Java.repository.MeterRepository;
import national_exam.Java.repository.NotificationRepository;
import national_exam.Java.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository customerRepository;
	private final MeterRepository meterRepository;
	private final MeterReadingRepository meterReadingRepository;
	private final BillRepository billRepository;
	private final PaymentRepository paymentRepository;
	private final NotificationRepository notificationRepository;

	@Transactional
	public CustomerResponse create(CustomerRequest request) {
		if (customerRepository.existsByNationalId(request.getNationalId())) {
			throw new BusinessException("Customer with National ID already exists");
		}
		if (customerRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException("Customer with email already exists");
		}

		Customer customer =
				Customer.builder()
						.fullNames(request.getFullNames())
						.nationalId(request.getNationalId())
						.email(request.getEmail())
						.phoneNumber(request.getPhoneNumber())
						.address(request.getAddress())
						.status(request.getStatus() != null ? request.getStatus() : AccountStatus.ACTIVE)
						.build();

		return toResponse(customerRepository.save(customer));
	}

	public List<CustomerResponse> getAll() {
		return customerRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
	}

	public CustomerResponse getById(Long id) {
		return toResponse(findCustomer(id));
	}

	@Transactional
	public CustomerResponse update(Long id, CustomerRequest request) {
		Customer customer = findCustomer(id);

		if (!customer.getNationalId().equals(request.getNationalId())
				&& customerRepository.existsByNationalId(request.getNationalId())) {
			throw new BusinessException("National ID already in use");
		}

		customer.setFullNames(request.getFullNames());
		customer.setNationalId(request.getNationalId());
		customer.setEmail(request.getEmail());
		customer.setPhoneNumber(request.getPhoneNumber());
		customer.setAddress(request.getAddress());
		if (request.getStatus() != null) {
			customer.setStatus(request.getStatus());
		}

		return toResponse(customerRepository.save(customer));
	}

	@Transactional
	public void delete(Long id) {
		Customer customer = findCustomer(id);

		List<Bill> bills = billRepository.findByCustomerId(id);
		for (Bill bill : bills) {
			paymentRepository.deleteAll(paymentRepository.findByBillId(bill.getId()));
		}
		notificationRepository.deleteAll(notificationRepository.findByCustomerIdOrderByCreatedAtDesc(id));
		billRepository.deleteAll(bills);

		List<Meter> meters = meterRepository.findByCustomerId(id);
		for (Meter meter : meters) {
			List<MeterReading> readings = meterReadingRepository.findByMeterId(meter.getId());
			meterReadingRepository.deleteAll(readings);
		}
		meterRepository.deleteAll(meters);
		customerRepository.delete(customer);
	}

	public Customer findCustomer(Long id) {
		return customerRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
	}

	private CustomerResponse toResponse(Customer customer) {
		return CustomerResponse.builder()
				.id(customer.getId())
				.fullNames(customer.getFullNames())
				.nationalId(customer.getNationalId())
				.email(customer.getEmail())
				.phoneNumber(customer.getPhoneNumber())
				.address(customer.getAddress())
				.status(customer.getStatus())
				.build();
	}
}
