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
public class CashAccount implements FinancialAccount {
    private String id;
    private BigDecimal amount;
}
