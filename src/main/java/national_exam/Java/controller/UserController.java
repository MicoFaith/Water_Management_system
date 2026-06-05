package national_exam.Java.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.auth.AuthResponse;
import national_exam.Java.dto.user.AdminUserRequest;
import national_exam.Java.dto.user.UserResponse;
import national_exam.Java.dto.user.UserStatusRequest;
import national_exam.Java.service.AuthService;
import national_exam.Java.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

	private final UserService userService;
	private final AuthService authService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public AuthResponse registerUser(@Valid @RequestBody AdminUserRequest request) {
		return authService.registerStaff(request);
	}

	@GetMapping
	public List<UserResponse> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{id}")
	public UserResponse getUser(@PathVariable Long id) {
		return userService.getUserById(id);
	}

	@PutMapping("/{id}/status")
	public UserResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UserStatusRequest request) {
		return userService.updateStatus(id, request);
	}
}
