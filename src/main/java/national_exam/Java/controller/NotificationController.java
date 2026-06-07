package national_exam.Java.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.notification.NotificationResponse;
import national_exam.Java.service.NotificationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications")
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
	@Operation(summary = "List all notifications — Allowed roles: ADMIN, FINANCE")
	public List<NotificationResponse> getAll() {
		return notificationService.getAll();
	}

	@GetMapping("/customer/{customerId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'CUSTOMER')")
	@Operation(summary = "List notifications for customer — Allowed roles: ADMIN, FINANCE, CUSTOMER")
	public List<NotificationResponse> getByCustomer(@PathVariable Long customerId) {
		return notificationService.getByCustomer(customerId);
	}

	@PutMapping("/{id}/read")
	@PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'CUSTOMER')")
	@Operation(summary = "Mark notification as read — Allowed roles: ADMIN, FINANCE, CUSTOMER")
	public NotificationResponse markAsRead(@PathVariable Long id) {
		return notificationService.markAsRead(id);
	}
}
