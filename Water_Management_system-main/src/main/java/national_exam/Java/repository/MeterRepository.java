package national_exam.Java.repository;

import java.util.List;
import java.util.Optional;
import national_exam.Java.entity.Meter;
import national_exam.Java.enums.MeterType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeterRepository extends JpaRepository<Meter, Long> {

	Optional<Meter> findByMeterNumber(String meterNumber);

	boolean existsByMeterNumber(String meterNumber);

	List<Meter> findByCustomerId(Long customerId);

	List<Meter> findByCustomerIdAndMeterType(Long customerId, MeterType meterType);
}
