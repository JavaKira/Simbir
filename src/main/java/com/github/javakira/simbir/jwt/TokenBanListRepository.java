package com.github.javakira.simbir.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBanListRepository extends JpaRepository<BannedToken, String> {
}
