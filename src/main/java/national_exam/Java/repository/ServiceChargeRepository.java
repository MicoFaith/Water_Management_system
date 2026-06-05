package national_exam.Java.repository;

import java.time.LocalDate;
import java.util.Optional;
import national_exam.Java.entity.ServiceCharge;
import national_exam.Java.enums.MeterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceChargeRepository extends JpaRepository<ServiceCharge, Long> {

	@Query(
			"""
			SELECT s FROM ServiceCharge s
			WHERE s.meterType = :meterType
			AND s.active = true
			AND s.effectiveFrom <= :billingDate
			AND (s.effectiveTo IS NULL OR s.effectiveTo >= :billingDate)
			ORDER BY s.version DESC
			""")
	Optional<ServiceCharge> findActiveForDate(
			@Param("meterType") MeterType meterType, @Param("billingDate") LocalDate billingDate);
}
