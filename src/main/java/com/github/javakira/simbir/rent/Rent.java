package com.github.javakira.simbir.rent;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Rent {
    public enum RentState {
        opened, ended
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
