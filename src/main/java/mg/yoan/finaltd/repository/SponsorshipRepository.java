package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.entity.Sponsorship;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SponsorshipRepository {

    public void save(Sponsorship sponsorship, Connection conn) {
        String sql = "INSERT INTO sponsorship (candidate_id, sponsor_id, collectivity_id, relation_type) " +
                     "VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sponsorship.getCandidateId());
            pstmt.setInt(2, sponsorship.getSponsorId());
            pstmt.setInt(3, sponsorship.getCollectivityId());
            pstmt.setString(4, sponsorship.getRelationType());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving sponsorship", e);
        }
    }
}
