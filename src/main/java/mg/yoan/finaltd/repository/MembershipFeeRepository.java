package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.entity.ActivityStatus;
import mg.yoan.finaltd.entity.Frequency;
import mg.yoan.finaltd.entity.MembershipFee;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class MembershipFeeRepository {

    public List<MembershipFee> findByCollectivityId(String collectivityId, Connection conn) {
        String sql = "SELECT * FROM membership_fee WHERE collectivity_id = ?";
        List<MembershipFee> fees = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, collectivityId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    fees.add(mapResultSetToFee(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching membership fees", e);
        }
        return fees;
    }

    public List<MembershipFee> saveAll(String collectivityId, List<MembershipFee> fees, Connection conn) {
        String sql = "INSERT INTO membership_fee (id, collectivity_id, eligible_from, frequency, amount, label, status) " +
                     "VALUES (?, ?, ?, ?::frequency, ?, ?, ?::activity_status) RETURNING *";
        List<MembershipFee> savedFees = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (MembershipFee fee : fees) {
                if (fee.getId() == null) {
                    fee.setId(UUID.randomUUID().toString());
                }
                pstmt.setString(1, fee.getId());
                pstmt.setString(2, collectivityId);
                pstmt.setObject(3, fee.getEligibleFrom() != null ? fee.getEligibleFrom() : LocalDate.now());
                pstmt.setString(4, fee.getFrequency() != null ? fee.getFrequency().name() : null);
                pstmt.setBigDecimal(5, fee.getAmount());
                pstmt.setString(6, fee.getLabel());
                pstmt.setString(7, ActivityStatus.ACTIVE.name());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        savedFees.add(mapResultSetToFee(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving membership fees", e);
        }
        return savedFees;
    }

    private MembershipFee mapResultSetToFee(ResultSet rs) throws SQLException {
        return MembershipFee.builder()
                .id(rs.getString("id"))
                .eligibleFrom(rs.getObject("eligible_from", LocalDate.class))
                .frequency(Frequency.valueOf(rs.getString("frequency")))
                .amount(rs.getBigDecimal("amount"))
                .label(rs.getString("label"))
                .status(ActivityStatus.valueOf(rs.getString("status")))
                .build();
    }
}
