package com.okhara.rating_system.web.dto.response.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GamesListResponse {

    private List<GameResponse> games;
}
