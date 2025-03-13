package com.okhara.rating_system.web.controller.admin;

import com.okhara.rating_system.mapper.SellerMapper;
import com.okhara.rating_system.service.admin.AdminAppUserService;
import com.okhara.rating_system.web.dto.response.seller.SellersListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Tag(name = "Admin users controller (Must be authenticated as ADMIN)", description = "Activation/deactivation of pending accounts")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class AdminAppUserController {

    private final AdminAppUserService adminAppUserService;
    private final SellerMapper sellerMapper;

    @GetMapping
    @Operation(summary = "Get pending accounts", description = "Return all pending accounts list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of pending accounts"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SellersListResponse> allPendingAccounts(@RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                                                  @RequestParam(defaultValue = "10") @Positive int size){
        return ResponseEntity.ok(sellerMapper.usersListToSellersListResponse(
                adminAppUserService.getAllPendingAccounts(PageRequest.of(page, size))
        ));
    }

    @PostMapping("/{id}")
    @Operation(summary = "Activate account", description = "Activate account by user id")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Account activated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "404", description = "Account not found or already activated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> activateAccount(@PathVariable @NotNull @Positive Long id){
        adminAppUserService.activateSellersAccount(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate account", description = "Deactivate pending account by user id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account deactivated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "404", description = "Account not found or already activated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deactivatePendingAccount(@PathVariable @NotNull @Positive Long id){
        adminAppUserService.deactivatePendingAccount(id);
        return ResponseEntity.noContent().build();
    }
}
