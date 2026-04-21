package mg.yoan.finaltd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sponsorship {
    private Integer id;
    private Integer candidateId;
    private Integer sponsorId;
    private Integer collectivityId;
    private String relationType;
}
