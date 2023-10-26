package com.github.javakira.simbir.transport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportDto {
    long id;
    long ownerId;
    private boolean canBeRented;
    private TransportType transportType;
    private String model;
    private String color;
    private String identifier;
    private String description;
    private double latitude;
    private double longitude;
    private Double minutePrice;
    private Double dayPrice;

    public static TransportDto from(Transport transport) {
        return TransportDto
                .builder()
                .id(transport.getId())
                .ownerId(transport.getOwnerId())
                .canBeRented(transport.isCanBeRented())
                .model(transport.getModel())
                .color(transport.getColor())
                .identifier(transport.getIdentifier())
                .description(transport.getDescription())
                .latitude(transport.getLatitude())
                .longitude(transport.getLongitude())
                .minutePrice(transport.getMinutePrice())
                .dayPrice(transport.getDayPrice())
                .build();
    }
}
