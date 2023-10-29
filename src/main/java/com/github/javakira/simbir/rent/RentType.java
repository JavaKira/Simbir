package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.transport.Transport;

import java.time.temporal.ChronoUnit;

public enum RentType {
    Minutes {
        @Override
        double price(Rent rent) {
            return ChronoUnit.MINUTES.between(
                    rent.getTimeStart().truncatedTo(ChronoUnit.MINUTES),
                    rent.getTimeEnd().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1)
            ) * rent.getPriceOfUnit();
        }

        @Override
        double priceUnit(Transport transport) {
            if (transport.getMinutePrice() == null)
                throw new IllegalArgumentException();

            return transport.getMinutePrice();
        }
    },
    Days {
        @Override
        double price(Rent rent) {
            return ChronoUnit.DAYS.between(
                    rent.getTimeStart().truncatedTo(ChronoUnit.DAYS),
                    rent.getTimeEnd().truncatedTo(ChronoUnit.DAYS).plusDays(1)
            ) * rent.getPriceOfUnit();
        }

        @Override
        double priceUnit(Transport transport) {
            if (transport.getDayPrice() == null)
                throw new IllegalArgumentException();

            return transport.getDayPrice();
        }
    };

    abstract double price(Rent rent);
    abstract double priceUnit(Transport transport) throws IllegalArgumentException;
}
