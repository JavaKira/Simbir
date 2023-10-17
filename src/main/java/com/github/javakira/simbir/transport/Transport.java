package com.github.javakira.simbir.transport;

import com.github.javakira.simbir.rent.Rent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private Long ownerId;
    @OneToMany
    List<Rent> rentHistory;
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
