package national_exam.Java.repository;

import java.util.List;
import java.util.Optional;
import national_exam.Java.entity.Bill;
import national_exam.Java.enums.BillStatus;
import national_exam.Java.enums.MeterType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Long> {

	List<Bill> findByCustomerId(Long customerId);

	List<Bill> findByStatus(BillStatus status);

	Optional<Bill> findByCustomerIdAndMeterTypeAndBillingMonthAndBillingYear(
			Long customerId, MeterType meterType, Integer month, Integer year);

	Optional<Bill> findByMeterIdAndBillingMonthAndBillingYear(
			Long meterId, Integer month, Integer year);
}
