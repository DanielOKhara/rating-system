package com.okhara.rating_system.web.controller.open;

import com.okhara.rating_system.mapper.GameObjectMapper;
import com.okhara.rating_system.mapper.SellerMapper;
import com.okhara.rating_system.service.open.AppUserOpenService;
import com.okhara.rating_system.service.open.GameObjectService;
import com.okhara.rating_system.web.dto.response.object.GameObjectsListResponse;
import com.okhara.rating_system.web.dto.response.seller.SellerResponse;
import com.okhara.rating_system.web.dto.response.seller.SellersListResponse;
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
public class SellersOpenController {

    private final AppUserOpenService userOpenService;
    private final GameObjectService gameObjectService;
    private final SellerMapper sellerMapper;
    private final GameObjectMapper gameObjectMapper;

    @GetMapping
    public ResponseEntity<SellersListResponse> getAllSellers(@RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                                             @RequestParam(defaultValue = "10") @Positive int size){
        return ResponseEntity.ok(sellerMapper.usersListToSellersListResponse(
                userOpenService.findAllSellers(PageRequest.of(page, size, Sort.by("nickname").ascending()))
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<SellerResponse> findSeller(@RequestParam @NotBlank String query) {
        if (query.matches("\\d+")) {
            return ResponseEntity.ok(sellerMapper.entityToResponse(userOpenService.findSellerById(Long.parseLong(query))));
        }
        return ResponseEntity.ok(sellerMapper.entityToResponse(userOpenService.findSellerByNickname(query)));
    }

    @GetMapping("/{sellerId}/profile")
    public ResponseEntity<SellerResponse> getSellersProfile(@PathVariable @NotBlank @Positive Long sellerId){
        return ResponseEntity.ok(sellerMapper.entityToResponse(userOpenService.findSellerById(sellerId)));
    }

    @GetMapping("/{sellerId}/profile/game_objects")
    public ResponseEntity<GameObjectsListResponse> getSellersGameObjects(@PathVariable @NotNull @Positive Long sellerId,
                                                                         @RequestParam(defaultValue = "0") @PositiveOrZero int page){
        return ResponseEntity.ok(gameObjectMapper.entityListToResponseList(
                gameObjectService.findGameObjectsBySeller(sellerId,
                        PageRequest.of(page, 15,Sort.by(Sort.Direction.DESC, "created_at")))));
    }

    @GetMapping("/top100")
    public ResponseEntity<SellersListResponse> getTopSellers() {
        return ResponseEntity.ok(sellerMapper.usersListToSellersListResponse(
                userOpenService.findTopSellers(PageRequest.of(0, 100))
        ));
    }

    @GetMapping("/active")
    public ResponseEntity<SellersListResponse> getAllActiveSellers(@RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                                                   @RequestParam(defaultValue = "10") @Positive int size){
        return ResponseEntity.ok(sellerMapper.usersListToSellersListResponse(
                userOpenService.findAllActiveSellers(PageRequest.of(page, size, Sort.by("nickname").ascending()))
        ));
    }

}
