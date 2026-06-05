package national_exam.Java.repository;

import java.util.List;
import java.util.Optional;
import national_exam.Java.entity.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {

	boolean existsByMeterIdAndBillingMonthAndBillingYear(Long meterId, Integer month, Integer year);

	Optional<MeterReading> findByMeterIdAndBillingMonthAndBillingYear(
			Long meterId, Integer month, Integer year);

	List<MeterReading> findByMeterCustomerIdAndBillingMonthAndBillingYear(
			Long customerId, Integer month, Integer year);

	List<MeterReading> findByMeterId(Long meterId);
}
