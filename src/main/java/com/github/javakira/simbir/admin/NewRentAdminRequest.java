package com.github.javakira.simbir.admin;

import com.github.javakira.simbir.rent.RentType;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewRentAdminRequest {
    //todo многое нужно в Rent подабавлять
    @NonNull
    private Long transportId;
    @NonNull
    private Long userId;
    @NonNull
    private String timeStart;
    private String timeEnd;
    @NonNull
    private Double priceOfUnit;
    @NonNull
    private RentType rentType;
    private Long finalPrice;
}
