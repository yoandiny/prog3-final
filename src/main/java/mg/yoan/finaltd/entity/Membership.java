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
public class Membership {
    private Integer id;
    private String memberId;
    private String collectivityId;
    private MemberStatus status;
    private LocalDate registrationDate;
}
