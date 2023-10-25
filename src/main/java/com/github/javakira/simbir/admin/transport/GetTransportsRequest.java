package com.github.javakira.simbir.admin.transport;

import com.github.javakira.simbir.transport.TransportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class GetTransportsRequest {
    //todo в пакете Rent такой же енам сделать либо в Transport запихнуть
    public enum SearchTransportType {
        Car, Bike, Scooter, Panzer, All;

        public boolean fits(TransportType type) {
            if (this == All)
                return true;

            return name().equals(type.name());
        }
    }

    private int start;
    private int count;
    private SearchTransportType searchTransportType;
}
