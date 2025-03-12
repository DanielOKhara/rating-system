package com.okhara.rating_system.web.controller.open;


import com.okhara.rating_system.mapper.GameMapper;
import com.okhara.rating_system.service.open.GameOpenService;
import com.okhara.rating_system.web.dto.response.game.GamesListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameOpenController {

    private final GameOpenService gameOpenService;

    private final GameMapper gameMapper;

    @GetMapping
    public ResponseEntity<GamesListResponse> getGamesList(){
        return ResponseEntity.ok(gameMapper.entityListToResponseList(gameOpenService.getAll()));
    }
}
