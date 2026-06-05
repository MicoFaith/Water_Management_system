package national_exam.Java.repository;

import java.time.LocalDate;
import java.util.Optional;
import national_exam.Java.entity.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

	@Query(
			"""
			SELECT p FROM Penalty p
			WHERE p.active = true
			AND p.effectiveFrom <= :billingDate
			AND (p.effectiveTo IS NULL OR p.effectiveTo >= :billingDate)
			ORDER BY p.version DESC
			""")
	Optional<Penalty> findActiveForDate(@Param("billingDate") LocalDate billingDate);
}
