package mg.yoan.finaltd.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collectivity {
    private Integer id;
    private String number;
    private String name;
    private String specialty;
    private String city;
    private Double annualFee;
}
