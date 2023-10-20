package com.github.javakira.simbir.admin.schema;

import com.github.javakira.simbir.transport.TransportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterTransportByAdminRequest {
    private long ownerId;
    private boolean canBeRented;
    private TransportType transportType;
    private String model;
    private String color;
    private String identifier;
    private String description; //todo may be null
    private double latitude;
    private double longitude;
    private Double minutePrice; //todo may be null
    private Double dayPrice; //todo may be null
}
