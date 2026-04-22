package mg.yoan.finaltd.service;

import mg.yoan.finaltd.entity.Collectivity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import mg.yoan.finaltd.repository.CollectivityRepository;

import mg.yoan.finaltd.config.DBConnection;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CollectivityService {

    private final CollectivityRepository repository;

    public List<Collectivity> createCollectivities(List<Collectivity> collectivities) {
        return repository.saveAll(collectivities);
    }

    public Collectivity assignIdentity(Integer id, String number, String name) {
        try (Connection conn = DBConnection.getConnection()) {
            Optional<Collectivity> collectivityOpt = repository.findById(id, conn);
            if (collectivityOpt.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collectivity not found");
            }
            Collectivity collectivity = collectivityOpt.get();

            if (collectivity.getNumber() != null || collectivity.getName() != null) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Identification already assigned and cannot be changed");
            }

            if (repository.findByName(name, conn).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Name already exists");
            }

            if (repository.findByNumber(number, conn).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Number already exists");
            }

            repository.updateIdentity(id, number, name, conn);
            collectivity.setNumber(number);
            collectivity.setName(name);
            return collectivity;
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
}
