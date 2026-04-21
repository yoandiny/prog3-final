package mg.yoan.finaltd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    private Integer id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String address;
    private String profession;
    private String phone;
    private String email;
    private LocalDate admissionDate;
}
