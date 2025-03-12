package com.okhara.rating_system.mapper;

import com.okhara.rating_system.model.marketplace.Game;
import com.okhara.rating_system.web.dto.response.game.GameResponse;
import com.okhara.rating_system.web.dto.response.game.GamesListResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameMapper {

    public GameResponse entityToResponse(Game game){
        return GameResponse.builder()
                .id(game.getId())
                .title(game.getTitle())
                .build();
    }

    public GamesListResponse entityListToResponseList(List<Game> games){
        GamesListResponse response = new GamesListResponse();
        response.setGames(games.stream().map(this::entityToResponse).toList());
        return response;
    }
}
