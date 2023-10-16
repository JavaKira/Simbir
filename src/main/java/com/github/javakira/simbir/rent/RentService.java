package com.github.javakira.simbir.rent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentService {
    private final RentRepository repository;

    public Optional<Rent> get(Long id) {
        return repository.findById(id);
    }
}
