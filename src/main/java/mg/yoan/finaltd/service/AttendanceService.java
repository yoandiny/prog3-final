package mg.yoan.finaltd.service;

import lombok.RequiredArgsConstructor;
import mg.yoan.finaltd.config.DBConnection;
import mg.yoan.finaltd.dto.AttendanceRequest;
import mg.yoan.finaltd.entity.Activity;
import mg.yoan.finaltd.entity.Attendance;
import mg.yoan.finaltd.repository.ActivityRepository;
import mg.yoan.finaltd.repository.AttendanceRepository;
import mg.yoan.finaltd.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final ActivityRepository activityRepository;
    private final MemberRepository memberRepository;

    public void markAttendance(String collectivityId, String activityId, List<AttendanceRequest> requests) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Check if activity exists and belongs to the collectivity
                Activity activity = activityRepository.findById(activityId, conn)
                        .orElseThrow(() -> new RuntimeException("Activity not found"));

                if (!activity.getCollectivityId().equals(collectivityId)) {
                    throw new RuntimeException("Activity does not belong to this collectivity");
                }

                List<Attendance> toSave = new ArrayList<>();
                for (AttendanceRequest request : requests) {
                    // 2. Check if member exists
                    memberRepository.findById(request.getMemberId(), conn)
                            .orElseThrow(() -> new RuntimeException("Member " + request.getMemberId() + " not found"));

                    // 3. Immutability Check: Check if attendance already exists for this member and activity
                    Optional<Attendance> existing = attendanceRepository.findByActivityIdAndMemberId(activityId, request.getMemberId(), conn);
                    if (existing.isPresent()) {
                        throw new RuntimeException("Attendance already marked for member " + request.getMemberId() + ". Modification is not allowed.");
                    }

                    toSave.add(Attendance.builder()
                            .activityId(activityId)
                            .memberId(request.getMemberId())
                            .status(request.getStatus())
                            .reason(request.getReason())
                            .build());
                }

                attendanceRepository.saveAll(toSave, conn);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Error marking attendance: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public List<Attendance> getAttendanceByActivity(String activityId) {
        try (Connection conn = DBConnection.getConnection()) {
            return attendanceRepository.findByActivityId(activityId, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
}
