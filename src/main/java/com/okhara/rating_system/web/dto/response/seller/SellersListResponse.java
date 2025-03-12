package com.okhara.rating_system.web.dto.response.seller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellersListResponse {

    private List<SellerResponse> sellers;
}
