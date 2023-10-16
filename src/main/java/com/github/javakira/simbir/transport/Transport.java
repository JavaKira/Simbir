package com.github.javakira.simbir.transport;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Transport {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String ownerUsername; //todo такой момент, что пользователь может просто поменять имя, тогда он потеряет всё в пизду
    @Column(nullable = false)
    private boolean canBeRented;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransportType transportType;
    @Column(nullable = false)
    private String model;
    @Column(nullable = false)
    private String color;
    @Column(nullable = false)
    private String identifier;
    private String description;
    @Column(nullable = false)
    private double latitude;
    @Column(nullable = false)
    private double longitude;
    private Double minutePrice;
    private Double dayPrice;
}
