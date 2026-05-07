package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.entity.Sponsorship;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class SponsorshipRepository {

    public void save(Sponsorship sponsorship, Connection conn) {
        String sql = "INSERT INTO sponsorship (id, candidate_id, sponsor_id, collectivity_id, relation_type) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sponsorship.getId() != null ? sponsorship.getId() : java.util.UUID.randomUUID().toString());
            pstmt.setString(2, sponsorship.getCandidateId());
            pstmt.setString(3, sponsorship.getSponsorId());
            pstmt.setString(4, sponsorship.getCollectivityId());
            pstmt.setString(5, sponsorship.getRelationType());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving sponsorship", e);
        }
    }
}
