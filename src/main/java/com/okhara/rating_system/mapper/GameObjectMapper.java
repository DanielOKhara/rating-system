package com.okhara.rating_system.mapper;

import com.okhara.rating_system.model.marketplace.GameObject;
import com.okhara.rating_system.web.dto.request.GameObjectRequest;
import com.okhara.rating_system.web.dto.response.object.GameObjectResponse;
import com.okhara.rating_system.web.dto.response.object.GameObjectsListResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameObjectMapper {

    public GameObject requestToEntity(GameObjectRequest gameObjectRequest){
        GameObject gameObject = new GameObject();
        gameObject.setTitle(gameObjectRequest.getTitle());
        gameObject.setDescription(gameObjectRequest.getDescription());
        return gameObject;
    }

    public GameObjectResponse entityToResponse(GameObject gameObject){
        return GameObjectResponse.builder()
                .id(gameObject.getId())
                .title(gameObject.getTitle())
                .description(gameObject.getDescription())
                .gameTitle(gameObject.getGame().getTitle())
                .sellerId(gameObject.getSeller().getId())
                .createdAt(gameObject.getCreatedAt())
                .build();
    }

    public GameObjectsListResponse entityListToResponseList(List<GameObject> gameObjects){
        GameObjectsListResponse response = new GameObjectsListResponse();
        response.setGameObjects(gameObjects.stream().map(this::entityToResponse).toList());
        return response;
    }
}
