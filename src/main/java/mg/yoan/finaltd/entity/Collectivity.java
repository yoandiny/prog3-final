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
public class Collectivity {
    private Integer id;
    private String number;
    private String name;
    private String specialty;
    private String city;
    private LocalDate creationDate;
    private BigDecimal annualFee;
}
