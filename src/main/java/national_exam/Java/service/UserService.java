package national_exam.Java.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import national_exam.Java.dto.user.UserResponse;
import national_exam.Java.dto.user.UserStatusRequest;
import national_exam.Java.entity.User;
import national_exam.Java.exception.ResourceNotFoundException;
import national_exam.Java.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public List<UserResponse> getAllUsers() {
		return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
	}

	public UserResponse getUserById(Long id) {
		return toResponse(findUser(id));
	}

	@Transactional
	public UserResponse updateStatus(Long id, UserStatusRequest request) {
		User user = findUser(id);
		user.setStatus(request.getStatus());
		return toResponse(userRepository.save(user));
	}

	private User findUser(Long id) {
		return userRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
	}

	private UserResponse toResponse(User user) {
		return UserResponse.builder()
				.id(user.getId())
				.fullNames(user.getFullNames())
				.email(user.getEmail())
				.phoneNumber(user.getPhoneNumber())
				.status(user.getStatus())
				.roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
				.build();
	}
}
