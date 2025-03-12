package com.okhara.rating_system.web.dto.response.object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameObjectResponse {

    private Long id;

    private String title;

    private String description;

    private Long sellerId;

    private String gameTitle;

    private Instant createdAt;

}
