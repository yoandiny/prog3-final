package mg.yoan.finaltd.controller;

import mg.yoan.finaltd.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import mg.yoan.finaltd.service.CollectivityService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/collectivities")
@AllArgsConstructor
public class CollectivityController {

    private final CollectivityService service;
 
    @GetMapping("/{id}")
    public Collectivity getCollectivityById(@PathVariable String id) {
        return service.getCollectivityById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<Collectivity> createCollectivities(@RequestBody List<CreateCollectivityRequest> requests) {
        return service.createCollectivities(requests);
    }

    @PutMapping("/{id}/informations")
    public Collectivity updateInformations(
            @PathVariable String id,
            @RequestBody InformationsRequest request) {
        return service.updateInformations(id, request.getNumber(), request.getName());
    }

    @GetMapping("/{id}/membershipFees")
    public List<MembershipFee> getMembershipFees(@PathVariable String id) {
        return service.getMembershipFees(id);
    }

    @PostMapping("/{id}/membershipFees")
    public List<MembershipFee> createMembershipFees(
            @PathVariable String id,
            @RequestBody List<MembershipFee> fees) {
        return service.createMembershipFees(id, fees);
    }

    @GetMapping("/{id}/transactions")
    public List<CollectivityTransaction> getTransactions(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.getTransactions(id, from, to);
    }

    @GetMapping("/{id}/financialAccounts")
    public List<FinancialAccount> getFinancialAccounts(
            @PathVariable String id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate at) {
        return service.getFinancialAccounts(id, at);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InformationsRequest {
        private Integer number;
        private String name;
    }
}
