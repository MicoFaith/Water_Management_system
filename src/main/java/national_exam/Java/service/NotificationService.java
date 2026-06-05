package national_exam.Java.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.notification.NotificationResponse;
import national_exam.Java.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;

	public List<NotificationResponse> getByCustomer(Long customerId) {
		return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
				.map(
						n ->
								NotificationResponse.builder()
										.id(n.getId())
										.customerId(n.getCustomer().getId())
										.billId(n.getBill() != null ? n.getBill().getId() : null)
										.message(n.getMessage())
										.notificationType(n.getNotificationType())
										.createdAt(n.getCreatedAt())
										.build())
				.collect(Collectors.toList());
	}

	public List<NotificationResponse> getAll() {
		return notificationRepository.findAll().stream()
				.map(
						n ->
								NotificationResponse.builder()
										.id(n.getId())
										.customerId(n.getCustomer().getId())
										.billId(n.getBill() != null ? n.getBill().getId() : null)
										.message(n.getMessage())
										.notificationType(n.getNotificationType())
										.createdAt(n.getCreatedAt())
										.build())
				.collect(Collectors.toList());
	}
}
