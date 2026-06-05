package national_exam.Java.repository;

import java.util.List;
import national_exam.Java.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	List<Payment> findByBillId(Long billId);

	List<Payment> findByBillCustomerId(Long customerId);
}
