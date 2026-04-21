package mg.yoan.finaltd.controller;

import mg.yoan.finaltd.entity.Collectivity;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import mg.yoan.finaltd.service.CollectivityService;

import java.util.List;

@RestController
@RequestMapping("/collectivities")
@AllArgsConstructor
public class CollectivityController {

    private final CollectivityService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<Collectivity> createCollectivities(@RequestBody List<Collectivity> collectivities) {
        return service.createCollectivities(collectivities);
    }
}
