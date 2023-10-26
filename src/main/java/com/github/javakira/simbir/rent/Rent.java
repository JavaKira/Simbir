package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.transport.Transport;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Rent {
    public enum RentState {
        opened, ended
    }

    //todo надо подумать над тем, что бы перенести в сервис
    public enum RentType {
        Minutes {
            @Override
            public double price(Rent rent) {
                return ChronoUnit.MINUTES.between(
                        rent.timeStart.truncatedTo(ChronoUnit.MINUTES),
                        rent.timeEnd.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1)
                ) * rent.priceOfUnit;
            }

            @Override
            public double priceUnit(Transport transport) {
                if (transport.getMinutePrice() == null)
                    throw new IllegalArgumentException();

                return transport.getMinutePrice();
            }
        },
        Days {
            @Override
            public double price(Rent rent) {
                return ChronoUnit.DAYS.between(
                        rent.timeStart.truncatedTo(ChronoUnit.DAYS),
                        rent.timeEnd.truncatedTo(ChronoUnit.DAYS).plusDays(1)
                ) * rent.priceOfUnit;
            }

            @Override
            public double priceUnit(Transport transport) {
                if (transport.getDayPrice() == null)
                    throw new IllegalArgumentException();

                return transport.getDayPrice();
            }
        };

        public abstract double price(Rent rent);
        public abstract double priceUnit(Transport transport) throws IllegalArgumentException;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Long ownerId;
    private Long transportId;
    @Enumerated(EnumType.STRING)
    private RentState rentState = RentState.opened;
    @Enumerated(EnumType.STRING)
    private RentType rentType;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private double priceOfUnit;
    private Double finalPrice;
}
