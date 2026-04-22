package mg.yoan.finaltd.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCollectivityRequest {
    private String location;
    private List<String> members;
    private Boolean federationApproval;
    private CreateStructureRequest structure;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateStructureRequest {
        private String president;
        private String vicePresident;
        private String treasurer;
        private String secretary;
    }
}
