package com.okhara.rating_system.mapper;

import com.okhara.rating_system.model.auth.AccountStatus;
import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.web.dto.response.seller.RatingResponse;
import com.okhara.rating_system.web.dto.response.seller.SellerResponse;
import com.okhara.rating_system.web.dto.response.seller.SellersListResponse;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class SellerMapper {

    public SellerResponse entityToResponse(AppUser user){
        return new SellerResponse(user.getId(),
                user.getNickname(),
                new RatingResponse(user.getRating().getRating(), user.getRating().getSumOfGrades()),
                createStatus(user.getStatus()),
                getAccountAge(user.getCreatedAt()));
    }

    public SellersListResponse usersListToSellersListResponse(List<AppUser> userList){
        SellersListResponse sellersListResponse = new SellersListResponse();
        sellersListResponse.setSellers(userList.stream().map(this::entityToResponse).toList());
        return sellersListResponse;
    }

    private String getAccountAge(Instant createdAt) {
        Duration duration = Duration.between(createdAt, Instant.now());
        long days = duration.toDays();
        long years = days / 365;
        long months = (days % 365) / 30;
        long remainingDays = (days % 365) % 30;

        return String.format("%d years, %d months, %d days", years, months, remainingDays);
    }

    private String createStatus(AccountStatus status){
        return status == AccountStatus.ACTIVE ? "Active" : "Placeholder";
    }

}
