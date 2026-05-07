package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.entity.Activity;
import mg.yoan.finaltd.entity.MemberOccupation;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ActivityRepository {

    public String save(Activity activity, Connection conn) throws SQLException {
        String sql = "INSERT INTO activity (id, label, activity_date, collectivity_id, is_mandatory, target_occupation) " +
                     "VALUES (?, ?, ?, ?, ?, ?::member_occupation) " +
                     "ON CONFLICT (id) DO UPDATE SET " +
                     "label = EXCLUDED.label, activity_date = EXCLUDED.activity_date, " +
                     "collectivity_id = EXCLUDED.collectivity_id, is_mandatory = EXCLUDED.is_mandatory, " +
                     "target_occupation = EXCLUDED.target_occupation " +
                     "RETURNING id";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String id = activity.getId() != null ? activity.getId() : UUID.randomUUID().toString();
            pstmt.setString(1, id);
            pstmt.setString(2, activity.getLabel());
            pstmt.setObject(3, activity.getDate());
            pstmt.setString(4, activity.getCollectivityId());
            pstmt.setBoolean(5, activity.isMandatory());
            pstmt.setObject(6, activity.getTargetOccupation() != null ? activity.getTargetOccupation().name() : null);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return null;
    }

    public List<String> saveAll(List<Activity> activities, Connection conn) throws SQLException {
        List<String> ids = new ArrayList<>();
        for (Activity activity : activities) {
            ids.add(save(activity, conn));
        }
        return ids;
    }

    public List<Activity> findByCollectivityId(String collectivityId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM activity WHERE collectivity_id = ?";
        List<Activity> activities = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, collectivityId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(mapResultSetToActivity(rs));
                }
            }
        }
        return activities;
    }

    public Optional<Activity> findById(String id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM activity WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToActivity(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Activity mapResultSetToActivity(ResultSet rs) throws SQLException {
        return Activity.builder()
                .id(rs.getString("id"))
                .label(rs.getString("label"))
                .date(rs.getObject("activity_date", LocalDate.class))
                .collectivityId(rs.getString("collectivity_id"))
                .mandatory(rs.getBoolean("is_mandatory"))
                .targetOccupation(rs.getString("target_occupation") != null ? 
                        MemberOccupation.valueOf(rs.getString("target_occupation")) : null)
                .build();
    }
}
