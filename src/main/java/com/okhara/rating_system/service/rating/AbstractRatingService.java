package com.okhara.rating_system.service.rating;

import com.okhara.rating_system.model.auth.AppUser;
import com.okhara.rating_system.repository.jpa.RatingRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractRatingService {

    private final RatingRepository ratingRepository;

    protected void updateRating(AppUser seller, boolean isAdding){
        //AppUser seller.getRating()
    }
}
