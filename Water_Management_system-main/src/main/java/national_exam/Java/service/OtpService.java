package national_exam.Java.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import national_exam.Java.entity.OtpVerification;
import national_exam.Java.exception.BusinessException;
import national_exam.Java.repository.OtpVerificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

	private final OtpVerificationRepository otpRepository;
	private final EmailService emailService;
	private final SecureRandom secureRandom = new SecureRandom();

	@Value("${otp.expiration-minutes:5}")
	private int expirationMinutes;

	@Transactional
	public void generateAndSendOtp(String email) {
		otpRepository.findByEmailAndVerifiedFalse(email).forEach(
				existing -> {
					existing.setVerified(true);
					otpRepository.save(existing);
				});

		String otpCode = String.format("%06d", secureRandom.nextInt(1_000_000));

		OtpVerification otp =
				OtpVerification.builder()
						.email(email)
						.otpCode(otpCode)
						.expiryTime(LocalDateTime.now().plusMinutes(expirationMinutes))
						.verified(false)
						.createdAt(LocalDateTime.now())
						.build();
		otpRepository.save(otp);

		emailService.sendOtpEmail(email, otpCode, expirationMinutes);
		log.info("OTP generated for {} (expires in {} minutes)", email, expirationMinutes);
	}

	@Transactional
	public void verifyOtp(String email, String otpCode) {
		OtpVerification otp =
				otpRepository
						.findTopByEmailAndOtpCodeAndVerifiedFalseOrderByCreatedAtDesc(email, otpCode)
						.orElseThrow(() -> new BusinessException("Invalid or expired OTP"));

		if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
			throw new BusinessException("OTP has expired. Please login again.");
		}

		otp.setVerified(true);
		otpRepository.save(otp);
	}
}
