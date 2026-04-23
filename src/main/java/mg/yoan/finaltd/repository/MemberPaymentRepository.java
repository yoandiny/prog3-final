package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.config.DBConnection;
import mg.yoan.finaltd.entity.MemberPayment;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class MemberPaymentRepository {

    public List<MemberPayment> saveAll(List<MemberPayment> payments, Connection connection) {
        String sql = "INSERT INTO member_payment (id, member_id, membership_fee_id, amount, payment_mode, account_credited_id, creation_date) " +
                     "VALUES (?, ?, ?, ?, ?::payment_mode, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (MemberPayment payment : payments) {
                if (payment.getId() == null) {
                    payment.setId(UUID.randomUUID().toString());
                }
                pstmt.setString(1, payment.getId());
                pstmt.setString(2, payment.getMember().getId().toString());
                pstmt.setString(3, payment.getMembershipFee() != null ? payment.getMembershipFee().getId() : null);
                pstmt.setBigDecimal(4, payment.getAmount());
                pstmt.setString(5, payment.getPaymentMode().name());
                pstmt.setString(6, payment.getAccountCreditedId());
                pstmt.setObject(7, payment.getCreationDate() != null ? payment.getCreationDate() : LocalDate.now());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            return payments;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving member payments", e);
        }
    }

    public List<MemberPayment> saveAll(List<MemberPayment> payments) {
        try (Connection conn = DBConnection.getConnection()) {
            return saveAll(payments, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error saving member payments", e);
        }
    }
}
