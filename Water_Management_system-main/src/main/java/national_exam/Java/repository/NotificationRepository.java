package national_exam.Java.repository;

import java.util.List;
import national_exam.Java.entity.Notification;
import national_exam.Java.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

	boolean existsByBillIdAndNotificationType(Long billId, NotificationType notificationType);
}
