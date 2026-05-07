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
public class Activity {
    private String id;
    private String label;
    private LocalDate date;
    private String collectivityId;
    private boolean mandatory;
    private MemberOccupation targetOccupation;
}
