package com.okhara.rating_system.web.controller.admin;

import com.okhara.rating_system.mapper.GameMapper;
import com.okhara.rating_system.service.admin.AdminGameService;
import com.okhara.rating_system.web.dto.request.CreatingGameRequest;
import com.okhara.rating_system.web.dto.response.game.GameResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin/create_game")
@RequiredArgsConstructor
public class AdminGameController {

    private final AdminGameService adminGameService;
    private final GameMapper gameMapper;

    @PostMapping
    public ResponseEntity<GameResponse> createGame(@RequestBody @Valid CreatingGameRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                gameMapper.entityToResponse(adminGameService.createGame(request)));
    }
}
