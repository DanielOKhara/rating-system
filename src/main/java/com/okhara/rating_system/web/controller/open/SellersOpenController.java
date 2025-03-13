package com.okhara.rating_system.web.controller.open;

import com.okhara.rating_system.mapper.GameObjectMapper;
import com.okhara.rating_system.mapper.SellerMapper;
import com.okhara.rating_system.service.open.AppUserOpenService;
import com.okhara.rating_system.service.open.GameObjectService;
import com.okhara.rating_system.web.dto.response.object.GameObjectsListResponse;
import com.okhara.rating_system.web.dto.response.seller.SellerResponse;
import com.okhara.rating_system.web.dto.response.seller.SellersListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor
@Tag(name = "Sellers controller (Open controller)", description = "Endpoints for retrieving information about sellers")

public class SellersOpenController {

    private final AppUserOpenService userOpenService;
    private final GameObjectService gameObjectService;
    private final SellerMapper sellerMapper;
    private final GameObjectMapper gameObjectMapper;


    @Operation(summary = "Get all sellers", description = "Returns a paginated list of all sellers.")
    @GetMapping
    public ResponseEntity<SellersListResponse> getAllSellers(@RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                                             @RequestParam(defaultValue = "10") @Positive int size){
        return ResponseEntity.ok(sellerMapper.usersListToSellersListResponse(
                userOpenService.findAllSellers(PageRequest.of(page, size, Sort.by("nickname").ascending()))
        ));
    }

    @Operation(summary = "Search seller", description = "Finds a seller by ID or nickname.")
    @GetMapping("/search")
    public ResponseEntity<SellerResponse> findSeller(@RequestParam @NotBlank String query) {
        if (query.matches("\\d+")) {
            return ResponseEntity.ok(sellerMapper.entityToResponse(userOpenService.findSellerById(Long.parseLong(query))));
        }
        return ResponseEntity.ok(sellerMapper.entityToResponse(userOpenService.findSellerByNickname(query)));
    }


    @Operation(summary = "Get seller's profile", description = "Returns the profile details of a seller by their ID.")
    @GetMapping("/{sellerId}/profile")
    public ResponseEntity<SellerResponse> getSellersProfile(@PathVariable @NotNull @Positive Long sellerId){
        return ResponseEntity.ok(sellerMapper.entityToResponse(userOpenService.findSellerById(sellerId)));
    }


    @Operation(summary = "Get seller's game objects", description = "Retrieves a paginated list of game objects created by a specific seller.")
    @GetMapping("/{sellerId}/profile/game_objects")
    public ResponseEntity<GameObjectsListResponse> getSellersGameObjects(@PathVariable @NotNull @Positive Long sellerId,
                                                                         @RequestParam(defaultValue = "0") @PositiveOrZero int page){
        return ResponseEntity.ok(gameObjectMapper.entityListToResponseList(
                gameObjectService.findGameObjectsBySeller(sellerId,
                        PageRequest.of(page, 15,Sort.by(Sort.Direction.DESC, "created_at")))));
    }

    @Operation(summary = "Get top 100 sellers", description = "Returns a list of the top 100 sellers based on their ratings.")
    @GetMapping("/top100")
    public ResponseEntity<SellersListResponse> getTopSellers() {
        return ResponseEntity.ok(sellerMapper.usersListToSellersListResponse(
                userOpenService.findTopSellers(PageRequest.of(0, 100))
        ));
    }

    @Operation(summary = "Get active sellers", description = "Returns a paginated list of currently active sellers.")
    @GetMapping("/active")
    public ResponseEntity<SellersListResponse> getAllActiveSellers(@RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                                                   @RequestParam(defaultValue = "10") @Positive int size){
        return ResponseEntity.ok(sellerMapper.usersListToSellersListResponse(
                userOpenService.findAllActiveSellers(PageRequest.of(page, size, Sort.by("nickname").ascending()))
        ));
    }

}
