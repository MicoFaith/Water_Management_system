package national_exam.Java.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.auth.AuthResponse;
import national_exam.Java.dto.auth.LoginRequest;
import national_exam.Java.dto.auth.OtpResponse;
import national_exam.Java.dto.auth.RegisterRequest;
import national_exam.Java.dto.auth.VerifyOtpRequest;
import national_exam.Java.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth (Public)")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Register a new customer account — Allowed roles: Public (no role)")
	public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
		return authService.register(request);
	}

	@PostMapping("/login")
	@Operation(summary = "Log in and request OTP — Allowed roles: Public (no role)")
	public OtpResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@PostMapping("/verify-otp")
	@Operation(summary = "Verify OTP and receive JWT — Allowed roles: Public (no role)")
	public AuthResponse verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
		return authService.verifyOtp(request);
	}
}
