package mg.yoan.finaltd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectivityOverallStatistics {
    private CollectivityInformation collectivityInformation;
    private Integer newMembersNumber;
    private Double overallMemberCurrentDuePercentage;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollectivityInformation {
        private String name;
        private Integer number;
    }
}
