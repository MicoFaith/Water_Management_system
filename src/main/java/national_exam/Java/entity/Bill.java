package national_exam.Java.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import national_exam.Java.enums.BillStatus;
import national_exam.Java.enums.MeterType;

@Entity
@Table(
		name = "bills",
		uniqueConstraints =
				@UniqueConstraint(columnNames = {"customer_id", "meter_type", "billing_month", "billing_year"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private MeterType meterType;

	@Column(nullable = false)
	private Integer billingMonth;

	@Column(nullable = false)
	private Integer billingYear;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal consumptionAmount;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal serviceChargeAmount;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal taxAmount;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal penaltyAmount;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal totalAmount;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal amountPaid;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal outstandingBalance;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private BillStatus status;

	@Column(nullable = false)
	private LocalDate dueDate;

	@Column(nullable = false)
	private LocalDate generatedDate;
}
