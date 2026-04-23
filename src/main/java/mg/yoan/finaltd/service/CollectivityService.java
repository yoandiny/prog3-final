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
import mg.yoan.finaltd.entity.FinancialAccount;
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
        if (memberIdStr == null) return null;
        try {
            Integer memberId = Integer.parseInt(memberIdStr);
            return memberRepository.findById(memberId, conn)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found: " + memberIdStr));
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
            collectivity.setNumber(number);
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
}
