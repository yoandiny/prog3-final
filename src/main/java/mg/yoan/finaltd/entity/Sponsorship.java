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
    private String candidateId;
    private String sponsorId;
    private String collectivityId;
    private String relationType;
}
