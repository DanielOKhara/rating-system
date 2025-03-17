package com.okhara.rating_system.service.admin;

import com.okhara.rating_system.aop.AuditLoggable;
import com.okhara.rating_system.model.marketplace.Game;
import com.okhara.rating_system.repository.jpa.GameRepository;
import com.okhara.rating_system.web.dto.request.CreatingGameRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminGameService {

    private final GameRepository gameRepository;

    @AuditLoggable
    public Game createGame(CreatingGameRequest creatingGameRequest){
        return gameRepository.save(Game.builder().title(creatingGameRequest.getGameTitle()).build());
    }
}
