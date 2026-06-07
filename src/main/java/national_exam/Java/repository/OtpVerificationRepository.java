package national_exam.Java.repository;

import java.util.List;
import java.util.Optional;
import national_exam.Java.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

	Optional<OtpVerification> findTopByEmailAndOtpCodeAndVerifiedFalseOrderByCreatedAtDesc(
			String email, String otpCode);

	List<OtpVerification> findByEmailAndVerifiedFalse(String email);
}
