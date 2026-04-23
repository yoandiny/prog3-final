package mg.yoan.finaltd.controller;

import mg.yoan.finaltd.entity.*;
import mg.yoan.finaltd.service.MemberService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<?> registerMembers(@RequestBody List<RegisterMemberRequest> requests) {
        try {
            for (RegisterMemberRequest req : requests) {
                Member member = Member.builder()
                        .firstName(req.getFirstName())
                        .lastName(req.getLastName())
                        .email(req.getEmail())
                        .birthDate(req.getBirthDate())
                        .gender(req.getGender())
                        .address(req.getAddress())
                        .profession(req.getProfession())
                        .admissionDate(LocalDate.now())
                        .build();

                List<Sponsorship> sponsorships = req.getSponsors().stream()
                        .map(s -> Sponsorship.builder()
                                .sponsorId(s.getSponsorId())
                                .relationType(s.getRelationNature())
                                .build())
                        .collect(Collectors.toList());

                memberService.registerMember(
                        member,
                        req.getCollectivityId(),
                        sponsorships,
                        req.getPaidAmount(),
                        req.getPaymentMode()
                );
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Members registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public List<MemberPayment> createPayments(@PathVariable String id, @RequestBody List<MemberPayment> payments) {
        return memberService.createPayments(id, payments);
    }

    @Data
    public static class RegisterMemberRequest {
        private String firstName;
        private String lastName;
        private String email;
        private LocalDate birthDate;
        private Gender gender;
        private String address;
        private String profession;
        private Integer collectivityId;
        private List<SponsorInput> sponsors;
        private BigDecimal paidAmount;
        private PaymentMode paymentMode;
    }

    @Data
    public static class SponsorInput {
        private Integer sponsorId;
        private String relationNature;
    }
}
