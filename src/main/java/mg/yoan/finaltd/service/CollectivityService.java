package mg.yoan.finaltd.service;

import mg.yoan.finaltd.config.DBConnection;
import mg.yoan.finaltd.entity.Collectivity;
import mg.yoan.finaltd.entity.CollectivityStructure;
import mg.yoan.finaltd.entity.CollectivityTransaction;
import mg.yoan.finaltd.entity.Member;
import mg.yoan.finaltd.entity.MembershipFee;
import mg.yoan.finaltd.controller.CreateCollectivityRequest;
import mg.yoan.finaltd.repository.CollectivityRepository;
import mg.yoan.finaltd.repository.CollectivityTransactionRepository;
import mg.yoan.finaltd.repository.MemberRepository;
import mg.yoan.finaltd.repository.MembershipFeeRepository;
import mg.yoan.finaltd.repository.FinancialAccountRepository;
import mg.yoan.finaltd.repository.MemberPaymentRepository;
import mg.yoan.finaltd.entity.FinancialAccount;
import mg.yoan.finaltd.entity.ActivityStatus;
import mg.yoan.finaltd.dto.CollectivityLocalStatistics;
import mg.yoan.finaltd.dto.CollectivityOverallStatistics;
import mg.yoan.finaltd.dto.MemberDescription;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CollectivityService {

    private final CollectivityRepository repository;
    private final MembershipFeeRepository feeRepository;
    private final CollectivityTransactionRepository transactionRepository;
    private final MemberRepository memberRepository;
    private final FinancialAccountRepository financialAccountRepository;
    private final MemberPaymentRepository memberPaymentRepository;

    public List<Collectivity> createCollectivities(List<CreateCollectivityRequest> requests) {
        try (Connection conn = DBConnection.getConnection()) {
            List<Collectivity> collectivities = new java.util.ArrayList<>();
            for (CreateCollectivityRequest request : requests) {
                if (request.getFederationApproval() == null || !request.getFederationApproval()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Federation approval is required");
                }
                if (request.getStructure() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Structure is required");
                }

                Member president = getMemberIfExists(request.getStructure().getPresident(), conn);
                Member vicePresident = getMemberIfExists(request.getStructure().getVicePresident(), conn);
                Member treasurer = getMemberIfExists(request.getStructure().getTreasurer(), conn);
                Member secretary = getMemberIfExists(request.getStructure().getSecretary(), conn);

                CollectivityStructure structure = CollectivityStructure.builder()
                        .president(president)
                        .vicePresident(vicePresident)
                        .treasurer(treasurer)
                        .secretary(secretary)
                        .build();

                Collectivity collectivity = Collectivity.builder()
                        .location(request.getLocation())
                        .structure(structure)
                        .build();

                collectivities.add(collectivity);
            }
            return repository.saveAll(collectivities, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    private Member getMemberIfExists(String memberIdStr, Connection conn) {
        if (memberIdStr == null)
            return null;
        try {
            Integer memberId = Integer.parseInt(memberIdStr);
            return memberRepository.findById(memberId, conn)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Member not found: " + memberIdStr));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found (invalid id): " + memberIdStr);
        }
    }

    public Collectivity updateInformations(String id, Integer number, String name) {
        try (Connection conn = DBConnection.getConnection()) {
            Optional<Collectivity> collectivityOpt = repository.findById(id, conn);
            if (collectivityOpt.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collectivity not found");
            }
            Collectivity collectivity = collectivityOpt.get();

            if (repository.findByName(name, conn).filter(c -> !c.getId().equals(id)).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name already used by other collectivity");
            }

            if (repository.findByNumber(number, conn).filter(c -> !c.getId().equals(id)).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number already used by other collectivity");
            }

            repository.updateInformations(id, number, name, conn);
            collectivity.setNumber(number != null ? number.toString() : null);
            collectivity.setName(name);
            return collectivity;
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public List<MembershipFee> getMembershipFees(String id) {
        try (Connection conn = DBConnection.getConnection()) {
            if (repository.findById(id, conn).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collectivity not found");
            }
            return feeRepository.findByCollectivityId(id, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public List<MembershipFee> createMembershipFees(String id, List<MembershipFee> fees) {
        try (Connection conn = DBConnection.getConnection()) {
            if (repository.findById(id, conn).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collectivity not found");
            }
            for (MembershipFee fee : fees) {
                if (fee.getAmount() == null || fee.getAmount().doubleValue() < 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount under 0");
                }
            }
            return feeRepository.saveAll(id, fees, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public List<CollectivityTransaction> getTransactions(String id, LocalDate from, LocalDate to) {
        try (Connection conn = DBConnection.getConnection()) {
            if (repository.findById(id, conn).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collectivity not found");
            }
            return transactionRepository.findByCollectivityIdAndPeriod(id, from, to, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public Collectivity getCollectivityById(String id) {
        try (Connection conn = DBConnection.getConnection()) {
            Collectivity collectivity = repository.findById(id, conn)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collectivity not found"));

            List<Member> members = memberRepository.findByCollectivityId(id, conn);
            for (Member member : members) {
                member.setReferees(memberRepository.findRefereesByMemberId(member.getId(), conn));
            }
            collectivity.setMembers(members);

            // Populate structure from members list based on occupation
            CollectivityStructure structure = new CollectivityStructure();
            for (Member m : members) {
                if (m.getOccupation() != null) {
                    switch (m.getOccupation()) {
                        case PRESIDENT -> structure.setPresident(m);
                        case VICE_PRESIDENT -> structure.setVicePresident(m);
                        case TREASURER -> structure.setTreasurer(m);
                        case SECRETARY -> structure.setSecretary(m);
                        case SENIOR, JUNIOR -> {} // No specific structure role
                    }
                }
            }
            collectivity.setStructure(structure);

            return collectivity;
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public List<FinancialAccount> getFinancialAccounts(String id, LocalDate at) {
        try (Connection conn = DBConnection.getConnection()) {
            if (repository.findById(id, conn).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collectivity not found");
            }
            return financialAccountRepository.findByCollectivityId(id, at, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public List<CollectivityLocalStatistics> getCollectivityStatistics(String id, LocalDate from, LocalDate to) {
        try (Connection conn = DBConnection.getConnection()) {
            if (repository.findById(id, conn).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collectivity not found");
            }
            List<Member> members = memberRepository.findByCollectivityId(id, conn);
            List<MembershipFee> fees = feeRepository.findByCollectivityId(id, conn);
            List<CollectivityLocalStatistics> stats = new java.util.ArrayList<>();

            for (Member member : members) {
                java.math.BigDecimal earned = memberPaymentRepository.getSumPaymentsByMemberAndPeriod(member.getId(), from, to, conn);
                java.math.BigDecimal due = java.math.BigDecimal.ZERO;
                for (MembershipFee fee : fees) {
                    if (fee.getStatus() == ActivityStatus.ACTIVE) {
                        long occurrences = calculateOccurrences(fee, member.getAdmissionDate(), from, to);
                        due = due.add(fee.getAmount().multiply(java.math.BigDecimal.valueOf(occurrences)));
                    }
                }
                java.math.BigDecimal unpaid = due.subtract(earned);
                if (unpaid.compareTo(java.math.BigDecimal.ZERO) < 0) unpaid = java.math.BigDecimal.ZERO;

                stats.add(CollectivityLocalStatistics.builder()
                        .memberDescription(MemberDescription.builder()
                                .id(member.getId().toString())
                                .firstName(member.getFirstName())
                                .lastName(member.getLastName())
                                .email(member.getEmail())
                                .occupation(member.getOccupation() != null ? member.getOccupation().name() : null)
                                .build())
                        .earnedAmount(earned.doubleValue())
                        .unpaidAmount(unpaid.doubleValue())
                        .build());
            }
            return stats;
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public List<CollectivityOverallStatistics> getOverallStatistics(LocalDate from, LocalDate to) {
        try (Connection conn = DBConnection.getConnection()) {
            List<Collectivity> allCollectivities = repository.findAll(conn);
            List<CollectivityOverallStatistics> stats = new java.util.ArrayList<>();

            for (Collectivity c : allCollectivities) {
                List<Member> members = memberRepository.findByCollectivityId(c.getId(), conn);
                List<MembershipFee> fees = feeRepository.findByCollectivityId(c.getId(), conn);

                int newMembers = 0;
                int currentMembers = 0;

                for (Member m : members) {
                    if (m.getAdmissionDate() != null && !m.getAdmissionDate().isBefore(from) && !m.getAdmissionDate().isAfter(to)) {
                        newMembers++;
                    }

                    java.math.BigDecimal earned = memberPaymentRepository.getSumPaymentsByMemberAndPeriod(m.getId(), from, to, conn);
                    java.math.BigDecimal due = java.math.BigDecimal.ZERO;
                    for (MembershipFee fee : fees) {
                        if (fee.getStatus() == ActivityStatus.ACTIVE) {
                            due = due.add(fee.getAmount().multiply(java.math.BigDecimal.valueOf(calculateOccurrences(fee, m.getAdmissionDate(), from, to))));
                        }
                    }

                    if (earned.compareTo(due) >= 0) {
                        currentMembers++;
                    }
                }

                double percentage = members.isEmpty() ? 0.0 : (double) currentMembers / members.size() * 100.0;

                stats.add(CollectivityOverallStatistics.builder()
                        .collectivityInformation(CollectivityOverallStatistics.CollectivityInformation.builder()
                                .name(c.getName())
                                .number(c.getNumber() != null ? Integer.parseInt(c.getNumber()) : null)
                                .build())
                        .newMembersNumber(newMembers)
                        .overallMemberCurrentDuePercentage(percentage)
                        .build());
            }
            return stats;
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    private long calculateOccurrences(MembershipFee fee, LocalDate admissionDate, LocalDate from, LocalDate to) {
        LocalDate start = fee.getEligibleFrom();
        if (admissionDate != null && admissionDate.isAfter(start)) {
            start = admissionDate;
        }
        if (from.isAfter(start)) {
            start = from;
        }

        if (start.isAfter(to)) {
            return 0;
        }

        switch (fee.getFrequency()) {
            case PUNCTUALLY:
                return (fee.getEligibleFrom().isAfter(from.minusDays(1)) && fee.getEligibleFrom().isBefore(to.plusDays(1))) ? 1 : 0;
            case WEEKLY:
                return java.time.temporal.ChronoUnit.WEEKS.between(start, to) + 1;
            case MONTHLY:
                return java.time.temporal.ChronoUnit.MONTHS.between(start, to) + 1;
            case ANNUALLY:
                return java.time.temporal.ChronoUnit.YEARS.between(start, to) + 1;
            default:
                return 0;
        }
    }
}
