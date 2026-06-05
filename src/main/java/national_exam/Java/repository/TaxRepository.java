package national_exam.Java.repository;

import java.time.LocalDate;
import java.util.Optional;
import national_exam.Java.entity.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaxRepository extends JpaRepository<Tax, Long> {

	@Query(
			"""
			SELECT t FROM Tax t
			WHERE t.active = true
			AND t.effectiveFrom <= :billingDate
			AND (t.effectiveTo IS NULL OR t.effectiveTo >= :billingDate)
			ORDER BY t.version DESC
			""")
	Optional<Tax> findActiveForDate(@Param("billingDate") LocalDate billingDate);
}
