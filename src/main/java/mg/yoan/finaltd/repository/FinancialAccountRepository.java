package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.entity.*;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Optional;

@Repository
public class FinancialAccountRepository {

    public Optional<FinancialAccount> findById(String id, Connection conn) {
        String sql = "SELECT * FROM financial_account WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFinancialAccount(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching financial account", e);
        }
        return Optional.empty();
    }

    private FinancialAccount mapResultSetToFinancialAccount(ResultSet rs) throws SQLException {
        String type = rs.getString("type");
        if ("CASH".equals(type)) {
            return CashAccount.builder()
                    .id(rs.getString("id"))
                    .amount(rs.getBigDecimal("amount"))
                    .build();
        } else if ("MOBILE_BANKING".equals(type)) {
            return MobileBankingAccount.builder()
                    .id(rs.getString("id"))
                    .holderName(rs.getString("holder_name"))
                    .mobileBankingService(rs.getString("mobile_service") != null ? MobileBankingService.valueOf(rs.getString("mobile_service")) : null)
                    .mobileNumber(rs.getObject("mobile_number", Integer.class))
                    .amount(rs.getBigDecimal("amount"))
                    .build();
        } else if ("BANK".equals(type)) {
            return BankAccount.builder()
                    .id(rs.getString("id"))
                    .holderName(rs.getString("holder_name"))
                    .bankName(rs.getString("bank_name") != null ? Bank.valueOf(rs.getString("bank_name")) : null)
                    .bankCode(rs.getObject("bank_code", Integer.class))
                    .bankBranchCode(rs.getObject("bank_branch_code", Integer.class))
                    .bankAccountNumber(rs.getObject("bank_account_number", Integer.class))
                    .bankAccountKey(rs.getObject("bank_account_key", Integer.class))
                    .amount(rs.getBigDecimal("amount"))
                    .build();
        }
        throw new RuntimeException("Unknown financial account type: " + type);
    }
}
