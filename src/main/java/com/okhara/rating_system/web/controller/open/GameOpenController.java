package com.okhara.rating_system.web.controller.open;


import com.okhara.rating_system.mapper.GameMapper;
import com.okhara.rating_system.service.open.GameOpenService;
import com.okhara.rating_system.web.dto.response.game.GamesListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
@Tag(name = "Games (Open controller)", description = "API for retrieving the list of games")
@RequiredArgsConstructor
public class GameOpenController {

    private final GameOpenService gameOpenService;

    private final GameMapper gameMapper;

    @Operation(
            summary = "Get the list of games",
            description = "Returns a list of all available games"
    )
    @GetMapping
    public ResponseEntity<GamesListResponse> getGamesList(){
        return ResponseEntity.ok(gameMapper.entityListToResponseList(gameOpenService.getAll()));
    }
}
