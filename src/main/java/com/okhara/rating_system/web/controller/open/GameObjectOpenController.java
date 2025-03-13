package com.okhara.rating_system.web.controller.open;

import com.okhara.rating_system.mapper.GameObjectMapper;
import com.okhara.rating_system.model.marketplace.GameObject;
import com.okhara.rating_system.service.open.GameObjectService;
import com.okhara.rating_system.web.dto.request.GameObjectRequest;
import com.okhara.rating_system.web.dto.response.object.GameObjectResponse;
import com.okhara.rating_system.web.dto.response.object.GameObjectsListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Game Objects (Open only to get objects)", description = "API for managing game objects in the marketplace")
public class GameObjectOpenController {

    private final GameObjectService gameObjectService;
    private final GameObjectMapper gameObjectMapper;


    @Operation(
            summary = "Get all game objects for a specific game",
            description = "Retrieves a paginated list of game objects related to a specific game."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved game objects list"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    @GetMapping
    public ResponseEntity<GameObjectsListResponse> getAllByGame(@PathVariable @NotNull @Positive Long gameId,
                                                                @RequestParam(defaultValue = "0") @PositiveOrZero int page) {
        return ResponseEntity.ok(gameObjectMapper.entityListToResponseList(
                gameObjectService.findGameObjectsByGame(
                        gameId, PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "created_at")))));
    }

    @Operation(
            summary = "Create a new game object",
            description = "Allows sellers to create a new game object within a specific game.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Game object successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PostMapping("/create")
    public ResponseEntity<GameObjectResponse> createGameObject(@PathVariable @NotNull @Positive Long gameId,
                                                               @RequestBody @Valid GameObjectRequest request) {
        GameObject gameObject = gameObjectService.createGameObject(gameId, gameObjectMapper.requestToEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                gameObjectMapper.entityToResponse(gameObject));
    }


    @Operation(
            summary = "Delete a game object",
            description = "Allows admins to delete a game object by its ID.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Game object successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Game object not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameObject(@PathVariable @NotNull @Positive Long id) {
        gameObjectService.deleteGameObjectById(id);
        return ResponseEntity.noContent().build();
    }
}
