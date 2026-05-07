package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.entity.CollectivityTransaction;
import mg.yoan.finaltd.entity.PaymentMode;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class CollectivityTransactionRepository {

    private final FinancialAccountRepository accountRepository = new FinancialAccountRepository();
    private final MemberRepository memberRepository = new MemberRepository();

    public List<CollectivityTransaction> findByCollectivityIdAndPeriod(String collectivityId, LocalDate from, LocalDate to, Connection conn) {
        String sql = "SELECT * FROM collectivity_transaction WHERE collectivity_id = ? AND creation_date BETWEEN ? AND ?";
        List<CollectivityTransaction> transactions = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, collectivityId);
            pstmt.setObject(2, from);
            pstmt.setObject(3, to);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs, conn));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching collectivity transactions", e);
        }
        return transactions;
    }

    public void save(String collectivityId, CollectivityTransaction transaction, Connection conn) {
        if (transaction.getId() == null) {
            transaction.setId(UUID.randomUUID().toString());
        }
        String sql = "INSERT INTO collectivity_transaction (id, collectivity_id, creation_date, amount, payment_mode, account_credited_id, member_debited_id) " +
                     "VALUES (?, ?, ?, ?, ?::payment_mode, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getId());
            pstmt.setString(2, collectivityId);
            pstmt.setObject(3, transaction.getCreationDate() != null ? transaction.getCreationDate() : LocalDate.now());
            pstmt.setBigDecimal(4, transaction.getAmount());
            pstmt.setString(5, transaction.getPaymentMode().name());
            pstmt.setString(6, transaction.getAccountCredited() != null ? transaction.getAccountCredited().getId() : null);
            pstmt.setObject(7, transaction.getMemberDebited() != null ? transaction.getMemberDebited().getId() : null);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving collectivity transaction", e);
        }
    }

    private CollectivityTransaction mapResultSetToTransaction(ResultSet rs, Connection conn) throws SQLException {
        return CollectivityTransaction.builder()
                .id(rs.getString("id"))
                .creationDate(rs.getObject("creation_date", LocalDate.class))
                .amount(rs.getBigDecimal("amount"))
                .paymentMode(PaymentMode.valueOf(rs.getString("payment_mode")))
                .accountCredited(accountRepository.findById(rs.getString("account_credited_id"), conn).orElse(null))
                .memberDebited(memberRepository.findById(rs.getString("member_debited_id"), conn).orElse(null))
                .build();
    }
}
