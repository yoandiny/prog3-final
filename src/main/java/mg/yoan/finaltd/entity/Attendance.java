package mg.yoan.finaltd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    private String id;
    private String activityId;
    private String memberId;
    private AttendanceStatus status;
    private String reason;
}
