package com.okhara.rating_system.service.open;

import com.okhara.rating_system.model.marketplace.Game;
import com.okhara.rating_system.repository.jpa.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameOpenService {

    private final GameRepository gameRepository;

    public List<Game> getAll(){
        return gameRepository.findAll();
    }
}
