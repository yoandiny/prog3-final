package mg.yoan.finaltd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private Integer id;
    private String memberId;
    private String collectivityId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private PaymentType type;
    private PaymentMode mode;
}
