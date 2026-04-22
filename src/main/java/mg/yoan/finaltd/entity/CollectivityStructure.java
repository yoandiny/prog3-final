package mg.yoan.finaltd.entity;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class CollectivityStructure {
    private Member president;
    private Member vicePresident;
    private Member treasurer;
    private Member secretary;
}
