package national_exam.Java.repository;

import java.util.Optional;
import national_exam.Java.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	boolean existsByNationalId(String nationalId);

	boolean existsByEmail(String email);

	Optional<Customer> findByNationalId(String nationalId);

	Optional<Customer> findByUserId(Long userId);
}
