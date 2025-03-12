package com.okhara.rating_system.web.controller.open;

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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/games/{gameId}/objects")
@RequiredArgsConstructor
public class GameObjectOpenController {

    private final GameObjectService gameObjectService;
    private final GameObjectMapper gameObjectMapper;

    @GetMapping
    public ResponseEntity<GameObjectsListResponse> getAllByGame(@PathVariable @NotNull @Positive Long gameId,
                                                                @RequestParam(defaultValue = "0") @PositiveOrZero int page) {
        return ResponseEntity.ok(gameObjectMapper.entityListToResponseList(
                gameObjectService.findGameObjectsByGame(
                        gameId, PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "created_at")))));
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PostMapping("/create")
    public ResponseEntity<GameObjectResponse> createGameObject(@PathVariable @NotNull @Positive Long gameId,
                                                               @RequestBody @Valid GameObjectRequest request) {
        GameObject gameObject = gameObjectService.createGameObject(gameId, gameObjectMapper.requestToEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                gameObjectMapper.entityToResponse(gameObject));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameObject(@PathVariable @NotNull @Positive Long id) {
        gameObjectService.deleteGameObjectById(id);
        return ResponseEntity.noContent().build();
    }
}
