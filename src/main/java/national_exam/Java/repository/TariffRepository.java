package national_exam.Java.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import national_exam.Java.entity.Tariff;
import national_exam.Java.enums.MeterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TariffRepository extends JpaRepository<Tariff, Long> {

	@Query(
			"""
			SELECT t FROM Tariff t
			WHERE t.meterType = :meterType
			AND t.active = true
			AND t.effectiveFrom <= :billingDate
			AND (t.effectiveTo IS NULL OR t.effectiveTo >= :billingDate)
			ORDER BY t.version DESC
			""")
	List<Tariff> findActiveTariffsForDate(
			@Param("meterType") MeterType meterType, @Param("billingDate") LocalDate billingDate);

	List<Tariff> findByMeterTypeOrderByVersionDesc(MeterType meterType);
}
