package com.github.javakira.simbir.transport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransportRepository extends JpaRepository<Transport, Long> {
    @NonNull
    Optional<Transport> findById(@NonNull Long id);
}
