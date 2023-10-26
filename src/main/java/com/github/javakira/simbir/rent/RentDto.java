package com.github.javakira.simbir.rent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RentDto {
    long id;
    long ownerId;
    long transportId;
    Rent.RentState state;
    Rent.RentType type;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private double priceOfUnit;
    private Double finalPrice;

    public static RentDto from(Rent rent) {
        return RentDto
                .builder()
                .id(rent.getId())
                .ownerId(rent.getOwnerId())
                .transportId(rent.getTransportId())
                .state(rent.getRentState())
                .type(rent.getRentType())
                .timeStart(rent.getTimeStart())
                .timeEnd(rent.getTimeEnd())
                .priceOfUnit(rent.getPriceOfUnit())
                .finalPrice(rent.getFinalPrice())
                .build();
    }
}
