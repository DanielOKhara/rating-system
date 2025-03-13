package com.okhara.rating_system.web.controller.seller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/account/my_game_objects")
@Tag(name = "Seller's Game Objects (Must be authenticated as SELLER)", description = "API for managing seller's own game objects")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasRole('ROLE_SELLER')")
@RequiredArgsConstructor
public class SellersGameObjectController {

    private final GameObjectService gameObjectService;
    private final GameObjectMapper gameObjectMapper;


    @Operation(
            summary = "Get seller's game objects",
            description = "Retrieves a paginated list of game objects created by the authenticated seller."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved seller's game objects"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @GetMapping
    public ResponseEntity<GameObjectsListResponse> getAllUsersGameObjects(@RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                                                          @RequestParam(defaultValue = "15") @Positive int size){
        return ResponseEntity.ok(gameObjectMapper.entityListToResponseList(
                gameObjectService.findAllUsersGameObjects(PageRequest.of(page, size))));
    }


    @Operation(
            summary = "Update a game object",
            description = "Allows the seller to update the title and description of a game object they own."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Game object successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Game object not found")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<GameObjectResponse> updateGameObject(@PathVariable @NotNull @Positive Long id,
                                                               @RequestBody @Valid GameObjectRequest updateRequest){
        GameObject updatedGameObject = gameObjectService.updateGameObject(id,
                updateRequest.getTitle(),
                updateRequest.getDescription());

        return ResponseEntity.ok(gameObjectMapper.entityToResponse(updatedGameObject));
    }


    @Operation(
            summary = "Delete a game object",
            description = "Allows the seller to delete their own game object."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Game object successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Game object not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameObject(@PathVariable @NotNull @Positive Long id){
        gameObjectService.deleteGameObjectById(id);
        return ResponseEntity.noContent().build();
    }

}
