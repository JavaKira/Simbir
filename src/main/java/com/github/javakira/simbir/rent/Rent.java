package com.github.javakira.simbir.rent;

import com.github.javakira.simbir.transport.Transport;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Rent {
    public enum RentState {
        opened, ended
    }

    public enum RentType {
        Minutes(rent -> ChronoUnit.SECONDS.between(rent.timeStart, rent.timeEnd) / 60.0 * rent.priceOfUnit),
        Days(rent -> ChronoUnit.SECONDS.between(rent.timeStart, rent.timeEnd) / 86400.0 * rent.priceOfUnit);

        final Function<Rent, Double> price;

        RentType(Function<Rent, Double> price) {
            this.price = price;
        }

        public double price(Rent rent) {
            return price.apply(rent);
        }
    }

    @Id
    @GeneratedValue
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
