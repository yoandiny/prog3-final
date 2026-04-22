package mg.yoan.finaltd.controller;

import mg.yoan.finaltd.entity.Collectivity;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    @PatchMapping("/{id}/identity")
    public Collectivity assignIdentity(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String authorization,
            @RequestBody IdentityRequest request) {
        return service.assignIdentity(id, request.getNumber(), request.getName());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdentityRequest {
        private String number;
        private String name;
    }
}
