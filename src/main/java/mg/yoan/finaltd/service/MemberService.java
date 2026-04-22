package mg.yoan.finaltd.service;

import mg.yoan.finaltd.config.DBConnection;
import mg.yoan.finaltd.entity.*;
import mg.yoan.finaltd.repository.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MemberService {

    private final MemberRepository memberRepository = new MemberRepository();
    private final CollectivityRepository collectivityRepository = new CollectivityRepository();
    private final MembershipRepository membershipRepository = new MembershipRepository();
    private final PaymentRepository paymentRepository = new PaymentRepository();
    private final SponsorshipRepository sponsorshipRepository = new SponsorshipRepository();

    public void registerMember(Member member, Integer targetCollectivityId, List<Sponsorship> sponsorships, BigDecimal paidAmount, PaymentMode paymentMode) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Fetch Target Collectivity
                collectivityRepository.findById(targetCollectivityId.toString(), conn)
                        .orElseThrow(() -> new RuntimeException("Collectivity not found"));

                // 2. Validate Sponsors
                validateSponsorships(targetCollectivityId, sponsorships, conn);

                // 3. Validate Payment (registration fee 50000)
                BigDecimal requiredAmount = new BigDecimal("50000");
                if (paidAmount == null || paidAmount.compareTo(requiredAmount) < 0) {
                    throw new RuntimeException("Insufficient payment. Required: " + requiredAmount);
                }

                // 4. Persistence
                Integer memberId = memberRepository.save(member, conn);
                member.setId(memberId);

                // Save Membership
                Membership membership = Membership.builder()
                        .memberId(memberId)
                        .collectivityId(targetCollectivityId)
                        .status(MemberStatus.JUNIOR)
                        .registrationDate(LocalDate.now())
                        .build();
                membershipRepository.save(membership, conn);

                // Save Sponsorships
                for (Sponsorship s : sponsorships) {
                    s.setCandidateId(memberId);
                    s.setCollectivityId(targetCollectivityId);
                    sponsorshipRepository.save(s, conn);
                }

                // Save Payment
                Payment payment = Payment.builder()
                        .memberId(memberId)
                        .collectivityId(targetCollectivityId)
                        .amount(paidAmount)
                        .paymentDate(LocalDateTime.now())
                        .type(PaymentType.ADMISSION_FEE)
                        .mode(paymentMode)
                        .build();
                paymentRepository.save(payment, conn);

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Registration failed: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during registration", e);
        }
    }

    private void validateSponsorships(Integer targetCollectivityId, List<Sponsorship> sponsorships, Connection conn) throws SQLException {
        if (sponsorships == null || sponsorships.size() < 2) {
            throw new RuntimeException("A candidate must have at least 2 sponsors.");
        }

        int targetSponsors = 0;
        int otherSponsors = 0;

        for (Sponsorship s : sponsorships) {
            boolean belongsToTarget = checkSponsorCollectivity(s.getSponsorId(), targetCollectivityId, conn);
            if (belongsToTarget) {
                targetSponsors++;
            } else {
                otherSponsors++;
            }
        }

        if (targetSponsors < otherSponsors) {
            throw new RuntimeException("Number of sponsors from target collectivity must be >= sponsors from other collectivities.");
        }
    }

    private boolean checkSponsorCollectivity(Integer sponsorId, Integer targetCollectivityId, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM membership WHERE member_id = ? AND collectivity_id = ?";
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sponsorId);
            pstmt.setInt(2, targetCollectivityId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
