package national_exam.Java.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "penalties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Penalty {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, precision = 5, scale = 2)
	private BigDecimal percentage;

	@Column(nullable = false)
	private Integer daysAfterDue;

	@Column(nullable = false)
	private Integer version;

	@Column(nullable = false)
	private LocalDate effectiveFrom;

	private LocalDate effectiveTo;

	@Column(nullable = false)
	private boolean active;
}
