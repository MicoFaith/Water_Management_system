package national_exam.Java.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.user.UserResponse;
import national_exam.Java.dto.user.UserStatusRequest;
import national_exam.Java.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Users (ADMIN)")
public class UserController {

	private final UserService userService;

	@GetMapping
	@Operation(summary = "List all users — Allowed roles: ADMIN")
	public List<UserResponse> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get user by ID — Allowed roles: ADMIN")
	public UserResponse getUser(@PathVariable Long id) {
		return userService.getUserById(id);
	}

	@PutMapping("/{id}/status")
	@Operation(summary = "Update user account status — Allowed roles: ADMIN")
	public UserResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UserStatusRequest request) {
		return userService.updateStatus(id, request);
	}
}
