package national_exam.Java.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.notification.NotificationResponse;
import national_exam.Java.entity.Bill;
import national_exam.Java.entity.Customer;
import national_exam.Java.entity.Notification;
import national_exam.Java.enums.NotificationType;
import national_exam.Java.exception.ResourceNotFoundException;
import national_exam.Java.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;

	@Transactional
	public void createBillNotification(
			Customer customer, Bill bill, NotificationType type, boolean onGeneration) {
		if (notificationRepository.existsByBillIdAndNotificationType(bill.getId(), type)) {
			return;
		}

		String period = bill.getBillingMonth() + "/" + bill.getBillingYear();
		String message =
				onGeneration
						? "Dear "
								+ customer.getFullNames()
								+ ", Your "
								+ period
								+ " utility bill of "
								+ bill.getTotalAmount()
								+ " FRW has been successfully processed."
						: "Dear "
								+ customer.getFullNames()
								+ ", Your "
								+ period
								+ " utility bill of "
								+ bill.getTotalAmount()
								+ " FRW has been approved.";

		Notification notification =
				Notification.builder()
						.customer(customer)
						.bill(bill)
						.message(message)
						.notificationType(type)
						.createdAt(LocalDateTime.now())
						.read(false)
						.build();
		notificationRepository.save(notification);
	}

	@Transactional
	public NotificationResponse markAsRead(Long id) {
		Notification notification =
				notificationRepository
						.findById(id)
						.orElseThrow(
								() -> new ResourceNotFoundException("Notification not found with id: " + id));
		notification.setRead(true);
		return toResponse(notificationRepository.save(notification));
	}

	public List<NotificationResponse> getByCustomer(Long customerId) {
		return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}

	public List<NotificationResponse> getAll() {
		return notificationRepository.findAll().stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
	}

	private NotificationResponse toResponse(Notification n) {
		return NotificationResponse.builder()
				.id(n.getId())
				.customerId(n.getCustomer().getId())
				.billId(n.getBill() != null ? n.getBill().getId() : null)
				.message(n.getMessage())
				.notificationType(n.getNotificationType())
				.createdAt(n.getCreatedAt())
				.read(n.isRead())
				.build();
	}
}
