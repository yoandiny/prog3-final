package mg.yoan.finaltd.service;

import mg.yoan.finaltd.entity.Collectivity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import mg.yoan.finaltd.repository.CollectivityRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class CollectivityService {

    private final CollectivityRepository repository;

    public List<Collectivity> createCollectivities(List<Collectivity> collectivities) {
        return repository.saveAll(collectivities);
    }
}
