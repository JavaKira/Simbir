package com.github.javakira.simbir.rent;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        Minutes(rent -> {
            return BigDecimal.valueOf(ChronoUnit.SECONDS.between(rent.timeStart, rent.timeEnd))
                .divide(BigDecimal.valueOf(60.0), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(rent.priceOfUnit)).doubleValue();
        }),
        Days(rent -> {
            return BigDecimal.valueOf(ChronoUnit.SECONDS.between(rent.timeStart, rent.timeEnd))
                    .divide(BigDecimal.valueOf(86400.0), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(rent.priceOfUnit)).doubleValue();
        });

        final Function<Rent, Double> price;

        RentType(Function<Rent, Double> price) {
            this.price = price;
        }

        public double price(Rent rent) {
            return price.apply(rent);
        }
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
