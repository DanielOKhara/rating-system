package com.okhara.rating_system.web.controller.seller;

import com.okhara.rating_system.mapper.GameObjectMapper;
import com.okhara.rating_system.model.marketplace.GameObject;
import com.okhara.rating_system.service.open.GameObjectService;
import com.okhara.rating_system.web.dto.request.GameObjectRequest;
import com.okhara.rating_system.web.dto.response.object.GameObjectResponse;
import com.okhara.rating_system.web.dto.response.object.GameObjectsListResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/account/my_game_objects")
@PreAuthorize("hasRole('ROLE_SELLER')")
@RequiredArgsConstructor
public class SellersGameObjectController {

    private final GameObjectService gameObjectService;
    private final GameObjectMapper gameObjectMapper;


    @GetMapping
    public ResponseEntity<GameObjectsListResponse> getAllUsersGameObjects(@RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                                                          @RequestParam(defaultValue = "15") @Positive int size){
        return ResponseEntity.ok(gameObjectMapper.entityListToResponseList(
                gameObjectService.findAllUsersGameObjects(PageRequest.of(page, size))));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GameObjectResponse> updateGameObject(@PathVariable @NotNull @Positive Long id,
                                                               @RequestBody @Valid GameObjectRequest updateRequest){
        GameObject updatedGameObject = gameObjectService.updateGameObject(id,
                updateRequest.getTitle(),
                updateRequest.getDescription());

        return ResponseEntity.ok(gameObjectMapper.entityToResponse(updatedGameObject));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameObject(@PathVariable @NotNull @Positive Long id){
        gameObjectService.deleteGameObjectById(id);
        return ResponseEntity.noContent().build();
    }

}
