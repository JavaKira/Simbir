package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.transport.TransportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RentSearchParams {
    private double lat;
    private double longitude;
    private double radius;
    private TransportType type;
}
