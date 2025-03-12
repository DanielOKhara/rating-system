package com.okhara.rating_system.service.open;

import com.okhara.rating_system.exception.CoordinationException;
import com.okhara.rating_system.exception.EntityNotExistException;
import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.model.auth.RoleType;
import com.okhara.rating_system.model.marketplace.Game;
import com.okhara.rating_system.model.marketplace.GameObject;
import com.okhara.rating_system.repository.jpa.GameObjectRepository;
import com.okhara.rating_system.repository.jpa.GameRepository;
import com.okhara.rating_system.security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameObjectService {

    private final GameObjectRepository gameObjectRepository;
    private final AuthenticationFacade authenticationFacade;
    private final GameRepository gameRepository;


    public List<GameObject> findGameObjectsByGame(Long gameId, Pageable pageable){
        return gameObjectRepository
                .findAllByGameIdOrderByCreatedAtDesc(gameId, pageable)
                .toList();
    }

    public List<GameObject> findGameObjectsBySeller(Long sellerId, Pageable pageable){
        return gameObjectRepository
                .findAllBySellerIdOrderByCreatedAtDesc(sellerId, pageable)
                .toList();
    }

    public List<GameObject> findAllUsersGameObjects(Pageable pageable){
        AppUser currentUser = authenticationFacade.getCurrentUser();
        return gameObjectRepository
                .findAllBySellerIdOrderByCreatedAtDesc(currentUser.getId(), pageable)
                .toList();
    }

    @Transactional
    public GameObject createGameObject(Long gameId, GameObject gameObject){
        AppUser creator = authenticationFacade.getCurrentUser();
        Game game = gameRepository.findById(gameId).orElseThrow(() ->
                new EntityNotExistException("You are trying to create a game object for a game that doesn't exist"));
        gameObject.setSeller(creator);
        gameObject.setGame(game);
        gameObjectRepository.save(gameObject);
        return gameObject;
    }

    @Transactional
    public GameObject updateGameObject(Long gameObjectId, String title, String description){
        AppUser currentUser = authenticationFacade.getCurrentUser();
        GameObject updatingGameObject = gameObjectRepository.findById(gameObjectId).orElseThrow(() ->
                new EntityNotExistException(MessageFormat.format("Game object with id:{0} not exist",
                        gameObjectId)));

        if(!updatingGameObject.getSeller().equals(currentUser)){
            throw new CoordinationException("Only creator can update game object!");
        }

        updatingGameObject.setTitle(title);
        updatingGameObject.setDescription(description);

        gameObjectRepository.save(updatingGameObject);

        return updatingGameObject;
    }

    @Transactional
    public void deleteGameObjectById(Long gameObjectId){
        AppUser currentUser = authenticationFacade.getCurrentUser();
        GameObject deletingGameObject = gameObjectRepository.findById(gameObjectId).orElseThrow(() ->
                new EntityNotExistException(MessageFormat.format("Game object with id:{0} not exist",
                        gameObjectId)));

        boolean isOwner = deletingGameObject.getSeller().equals(currentUser);
        boolean isAdmin = currentUser.getRoles().contains(RoleType.ROLE_ADMIN);

        if(!isOwner && !isAdmin){
            throw new CoordinationException("Only creator or admin can update game object!");
        }

        gameObjectRepository.delete(deletingGameObject);
    }
}
