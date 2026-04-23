package mg.yoan.finaltd.repository;

import mg.yoan.finaltd.entity.*;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.math.BigDecimal;

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


    private FinancialAccount adjustBalanceToDate(FinancialAccount account, LocalDate at, Connection conn) {
        String sql = "SELECT SUM(amount) FROM collectivity_transaction WHERE account_credited_id = ? AND creation_date > ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account.getId());
            pstmt.setObject(2, at);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal transactionsAfter = rs.getBigDecimal(1);
                    if (transactionsAfter != null) {
                        BigDecimal adjustedAmount = account.getAmount().subtract(transactionsAfter);
                        return updateAccountAmount(account, adjustedAmount);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adjusting balance", e);
        }
        return account;
    }

    private FinancialAccount updateAccountAmount(FinancialAccount account, BigDecimal newAmount) {
        if (account instanceof CashAccount) {
            return CashAccount.builder()
                    .id(account.getId())
                    .amount(newAmount)
                    .build();
        } else if (account instanceof MobileBankingAccount) {
            MobileBankingAccount mba = (MobileBankingAccount) account;
            return MobileBankingAccount.builder()
                    .id(mba.getId())
                    .holderName(mba.getHolderName())
                    .mobileBankingService(mba.getMobileBankingService())
                    .mobileNumber(mba.getMobileNumber())
                    .amount(newAmount)
                    .build();
        } else if (account instanceof BankAccount) {
            BankAccount ba = (BankAccount) account;
            return BankAccount.builder()
                    .id(ba.getId())
                    .holderName(ba.getHolderName())
                    .bankName(ba.getBankName())
                    .bankCode(ba.getBankCode())
                    .bankBranchCode(ba.getBankBranchCode())
                    .bankAccountNumber(ba.getBankAccountNumber())
                    .bankAccountKey(ba.getBankAccountKey())
                    .amount(newAmount)
                    .build();
        }
        return account;
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
