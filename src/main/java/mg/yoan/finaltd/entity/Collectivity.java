package mg.yoan.finaltd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Collectivity {
    private String id;
    private String name;
    private String number;
    private String location;
    private CollectivityStructure structure;
    private List<Member> members;
}
