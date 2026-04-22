package mg.yoan.finaltd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipFee {
    private String id;
    private LocalDate eligibleFrom;
    private Frequency frequency;
    private BigDecimal amount;
    private String label;
    private ActivityStatus status;
}
