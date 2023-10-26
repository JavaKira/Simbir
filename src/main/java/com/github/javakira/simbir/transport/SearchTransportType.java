package com.github.javakira.simbir.transport;

public enum SearchTransportType {
    Car, Bike, Scooter, Panzer, All;

    public boolean fits(TransportType type) {
        if (this == All)
            return true;

        return name().equals(type.name());
    }
}
