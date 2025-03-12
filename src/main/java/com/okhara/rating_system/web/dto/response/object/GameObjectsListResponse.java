package com.okhara.rating_system.web.dto.response.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameObjectsListResponse {

    List<GameObjectResponse> gameObjects;

}
