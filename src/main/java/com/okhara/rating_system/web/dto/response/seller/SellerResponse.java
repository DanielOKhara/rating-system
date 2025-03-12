package com.okhara.rating_system.web.dto.response.seller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerResponse {

    private Long sellersId;

    private String nickname;

    private RatingResponse rating;

    private String status;

    private String accountAge;
}
