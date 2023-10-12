package com.github.javakira.simbir.transport;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportUpdateRequest {
    private boolean canBeRented;
    @NonNull
    private String model;
    @NonNull
    private String color;
    @NonNull
    private String identifier;
    @NonNull
    private String description;
    private double latitude;
    private double longitude;
    private Double minutePrice;
    private Double dayPrice;
}
