package com.github.javakira.simbir.admin.rent;

import com.github.javakira.simbir.rent.Rent;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRentAdminRequest {
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
    private Rent.RentType rentType;
    private Double finalPrice;
}

