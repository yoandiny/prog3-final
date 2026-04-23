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
public class MemberPayment {
    private String id;
    private Member member;
    private MembershipFee membershipFee;
    private BigDecimal amount;
    private PaymentMode paymentMode;
    private String accountCreditedId;
    private LocalDate creationDate;
}
