package national_exam.Java.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import national_exam.Java.enums.MeterType;
import national_exam.Java.enums.TariffType;

@Entity
@Table(name = "tariffs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tariff {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private MeterType meterType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TariffType tariffType;

	@Column(precision = 12, scale = 2)
	private BigDecimal flatRate;

	@Column(nullable = false)
	private Integer version;

	@Column(nullable = false)
	private LocalDate effectiveFrom;

	private LocalDate effectiveTo;

	@Column(nullable = false)
	private boolean active;

	@OneToMany(mappedBy = "tariff", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<TariffTier> tiers = new ArrayList<>();
}
