package national_exam.Java.repository;

import java.util.Optional;
import national_exam.Java.entity.Role;
import national_exam.Java.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(RoleName name);
}
