package mg.yoan.finaltd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobileBankingAccount implements FinancialAccount {
    private String id;
    private String holderName;
    private MobileBankingService mobileBankingService;
    private Integer mobileNumber;
    private BigDecimal amount;
}
