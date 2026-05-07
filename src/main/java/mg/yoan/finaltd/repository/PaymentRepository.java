package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.entity.Payment;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Repository
public class PaymentRepository {

    public void save(Payment payment, Connection conn) {
        String sql = "INSERT INTO payment (member_id, collectivity_id, amount, payment_date, type, mode) " +
                     "VALUES (?, ?, ?, ?, ?::payment_type, ?::payment_mode)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, payment.getMemberId());
            pstmt.setString(2, payment.getCollectivityId());
            pstmt.setBigDecimal(3, payment.getAmount());
            pstmt.setObject(4, payment.getPaymentDate() != null ? payment.getPaymentDate() : LocalDateTime.now());
            pstmt.setString(5, payment.getType().name());
            pstmt.setString(6, payment.getMode().name());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving payment", e);
        }
    }
}
