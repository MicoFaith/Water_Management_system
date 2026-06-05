package national_exam.Java.controller;

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
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
		return authService.register(request);
	}

	@PostMapping("/login")
	public OtpResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@PostMapping("/verify-otp")
	public AuthResponse verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
		return authService.verifyOtp(request);
	}
}
