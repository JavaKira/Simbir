package com.github.javakira.simbir.rent;

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
public class Rent {
    //todo нужно ещё время начала аренды добавить чтобы расчитывать время аренды и снимать деньги
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private RentType rentType;
    private Long ownerId;
    private Long transportId;
}
