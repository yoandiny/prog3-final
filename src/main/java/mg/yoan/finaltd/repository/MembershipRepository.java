package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.entity.Membership;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

@Repository
public class MembershipRepository {

    public void save(Membership membership, Connection conn) {
        String sql = "INSERT INTO membership (id, member_id, collectivity_id, status, registration_date) " +
                     "VALUES (?, ?, ?, ?::member_status, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, membership.getId() != null ? membership.getId() : java.util.UUID.randomUUID().toString());
            pstmt.setString(2, membership.getMemberId());
            pstmt.setString(3, membership.getCollectivityId());
            pstmt.setString(4, membership.getStatus() != null ? membership.getStatus().name() : "JUNIOR");
            pstmt.setObject(5, membership.getRegistrationDate() != null ? membership.getRegistrationDate() : LocalDate.now());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving membership", e);
        }
    }
}
