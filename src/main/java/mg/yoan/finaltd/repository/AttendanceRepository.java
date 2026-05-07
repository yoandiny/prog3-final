package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.entity.Attendance;
import mg.yoan.finaltd.entity.AttendanceStatus;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AttendanceRepository {

    public String save(Attendance attendance, Connection conn) throws SQLException {
        String sql = "INSERT INTO attendance (id, activity_id, member_id, status, reason) " +
                     "VALUES (?, ?, ?, ?::attendance_status, ?) " +
                     "ON CONFLICT (activity_id, member_id) DO NOTHING " +
                     "RETURNING id";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String id = attendance.getId() != null ? attendance.getId() : UUID.randomUUID().toString();
            pstmt.setString(1, id);
            pstmt.setString(2, attendance.getActivityId());
            pstmt.setString(3, attendance.getMemberId());
            pstmt.setObject(4, attendance.getStatus().name());
            pstmt.setString(5, attendance.getReason());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return null;
    }

    public List<String> saveAll(List<Attendance> attendances, Connection conn) throws SQLException {
        List<String> ids = new ArrayList<>();
        for (Attendance attendance : attendances) {
            String id = save(attendance, conn);
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }

    public List<Attendance> findByActivityId(String activityId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM attendance WHERE activity_id = ?";
        List<Attendance> attendances = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, activityId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    attendances.add(mapResultSetToAttendance(rs));
                }
            }
        }
        return attendances;
    }

    public Optional<Attendance> findByActivityIdAndMemberId(String activityId, String memberId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM attendance WHERE activity_id = ? AND member_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, activityId);
            pstmt.setString(2, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAttendance(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException {
        return Attendance.builder()
                .id(rs.getString("id"))
                .activityId(rs.getString("activity_id"))
                .memberId(rs.getString("member_id"))
                .status(AttendanceStatus.valueOf(rs.getString("status")))
                .reason(rs.getString("reason"))
                .build();
    }
}
