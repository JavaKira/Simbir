package com.github.javakira.simbir.admin.transport;

import com.github.javakira.simbir.transport.TransportType;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTransportByAdminRequest {
    @NonNull
    private Long ownerId;
    @NonNull
    private Boolean canBeRented;
    @NonNull
    private TransportType transportType;
    @NonNull
    private String model;
    @NonNull
    private String color;
    @NonNull
    private String identifier;
    private String description;
    @NonNull
    private Double latitude;
    @NonNull
    private Double longitude;
    private Double minutePrice;
    private Double dayPrice;
}
