package com.okhara.rating_system.web.controller.admin;

import com.okhara.rating_system.mapper.SellerMapper;
import com.okhara.rating_system.service.admin.AdminAppUserService;
import com.okhara.rating_system.web.dto.response.seller.SellersListResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Validated
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin/pending_accounts")
@RequiredArgsConstructor
public class AdminAppUserController {

    private final AdminAppUserService adminAppUserService;
    private final SellerMapper sellerMapper;

    @GetMapping
    public ResponseEntity<SellersListResponse> allPendingAccounts(@RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                                                  @RequestParam(defaultValue = "10") @Positive int size){
        return ResponseEntity.ok(sellerMapper.usersListToSellersListResponse(
                adminAppUserService.getAllPendingAccounts(PageRequest.of(page, size))
        ));
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> activateAccount(@PathVariable @NotBlank @Positive Long id){
        adminAppUserService.activateSellersAccount(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivatePendingAccount(@PathVariable @NotBlank @Positive Long id){
        adminAppUserService.deactivatePendingAccount(id);
        return ResponseEntity.noContent().build();
    }
}
