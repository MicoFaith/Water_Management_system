package national_exam.Java.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import national_exam.Java.entity.Bill;
import national_exam.Java.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

	private final JavaMailSender mailSender;

	@Value("${spring.mail.username:}")
	private String fromEmail;

	@Autowired(required = false)
	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendOtpEmail(String toEmail, String otpCode, int expiryMinutes) {
		String body =
				"Dear User,\n\n"
						+ "Your login verification code is:\n\n"
						+ otpCode
						+ "\n\nThis code expires in "
						+ expiryMinutes
						+ " minutes.\n\nUtility Billing System";
		sendEmail(toEmail, "Utility Billing System Login OTP", body);
	}

	public void sendBillGeneratedEmail(Customer customer, Bill bill) {
		String period = formatPeriod(bill.getBillingMonth(), bill.getBillingYear());
		String body =
				"Dear "
						+ firstName(customer.getFullNames())
						+ ",\n\nYour "
						+ period
						+ " utility bill of "
						+ bill.getTotalAmount()
						+ " FRW has been successfully generated.\n\nOutstanding Balance: "
						+ bill.getOutstandingBalance()
						+ " FRW";
		sendEmail(customer.getEmail(), "Utility Bill Generated - " + period, body);
	}

	public void sendBillApprovalEmail(Customer customer, Bill bill) {
		String period = formatPeriod(bill.getBillingMonth(), bill.getBillingYear());
		String body =
				"Dear "
						+ firstName(customer.getFullNames())
						+ ",\n\nYour "
						+ period
						+ " utility bill of "
						+ bill.getTotalAmount()
						+ " FRW has been approved.\n\nDue date: "
						+ bill.getDueDate()
						+ "\n\nOutstanding Balance: "
						+ bill.getOutstandingBalance()
						+ " FRW\n\nThank you,\nWASAC/REG";
		sendEmail(customer.getEmail(), "WASAC Utility Bill Approved - " + period, body);
	}

	public void sendPartialPaymentEmail(Customer customer, Bill bill, BigDecimal amountPaid) {
		String body =
				"Dear "
						+ firstName(customer.getFullNames())
						+ ",\n\nWe have received your payment of "
						+ amountPaid
						+ " FRW.\n\nRemaining Balance: "
						+ bill.getOutstandingBalance()
						+ " FRW";
		sendEmail(customer.getEmail(), "Partial Payment Received", body);
	}

	public void sendFullPaymentEmail(Customer customer, Bill bill) {
		String body =
				"Dear "
						+ firstName(customer.getFullNames())
						+ ",\n\nYour bill has been fully paid.\n\nBalance: 0 FRW\n\nThank you.";
		sendEmail(customer.getEmail(), "Bill Fully Paid", body);
	}

	private void sendEmail(String toEmail, String subject, String body) {
		if (mailSender == null) {
			log.warn("JavaMailSender not configured — skipping email to {}", toEmail);
			return;
		}
		if (toEmail == null || toEmail.isBlank()) {
			log.warn("No recipient email — skipping: {}", subject);
			return;
		}
		if (fromEmail == null || fromEmail.isBlank()) {
			log.warn("Mail sender not configured — skipping email to {}", toEmail);
			return;
		}

		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(fromEmail);
			message.setTo(toEmail);
			message.setSubject(subject);
			message.setText(body);
			mailSender.send(message);
			log.info("Email sent to {} — {}", toEmail, subject);
		} catch (Exception ex) {
			log.error("Failed to send email to {} ({}): {}", toEmail, subject, ex.getMessage());
		}
	}

	private String firstName(String fullNames) {
		if (fullNames == null || fullNames.isBlank()) {
			return "Customer";
		}
		return fullNames.trim().split("\\s+")[0];
	}

	private String formatPeriod(int month, int year) {
		return YearMonth.of(year, month)
				.getMonth()
				.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
				+ " "
				+ year;
	}
}
