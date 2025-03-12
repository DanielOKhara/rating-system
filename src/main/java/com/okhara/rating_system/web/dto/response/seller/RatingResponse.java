package com.okhara.rating_system.web.dto.response.seller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {

    private BigDecimal rating;

    private Long commentsCount;
}
