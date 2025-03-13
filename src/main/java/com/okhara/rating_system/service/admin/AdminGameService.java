package com.okhara.rating_system.service.admin;

import com.okhara.rating_system.model.marketplace.Game;
import com.okhara.rating_system.repository.jpa.GameRepository;
import com.okhara.rating_system.web.dto.request.CreatingGameRequest;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminGameService {

    private final GameRepository gameRepository;

    public Game createGame(CreatingGameRequest creatingGameRequest){
        return gameRepository.save(Game.builder().title(creatingGameRequest.getGameTitle()).build());
    }
}
