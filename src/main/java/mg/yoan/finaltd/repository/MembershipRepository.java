package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.entity.Membership;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class MembershipRepository {

    public void save(Membership membership, Connection conn) {
        String sql = "INSERT INTO membership (member_id, collectivity_id, status, registration_date) " +
                     "VALUES (?, ?, ?::member_status, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, membership.getMemberId());
            pstmt.setInt(2, membership.getCollectivityId());
            pstmt.setString(3, membership.getStatus() != null ? membership.getStatus().name() : "JUNIOR");
            pstmt.setObject(4, membership.getRegistrationDate() != null ? membership.getRegistrationDate() : LocalDate.now());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving membership", e);
        }
    }
}
