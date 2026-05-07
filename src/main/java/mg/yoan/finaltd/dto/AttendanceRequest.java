package mg.yoan.finaltd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mg.yoan.finaltd.entity.AttendanceStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequest {
    private String memberId;
    private AttendanceStatus status;
    private String reason;
}
