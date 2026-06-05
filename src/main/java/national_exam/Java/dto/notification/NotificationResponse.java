package national_exam.Java.dto.notification;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import national_exam.Java.enums.NotificationType;

@Data
@Builder
public class NotificationResponse {

	private Long id;
	private Long customerId;
	private Long billId;
	private String message;
	private NotificationType notificationType;
	private LocalDateTime createdAt;
}
