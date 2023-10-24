package com.github.javakira.simbir.jwt;

import com.github.javakira.simbir.account.Account;
import com.github.javakira.simbir.account.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String secret = "50861a2a1b08cd5f578facf25f0ad207831cafd0800ca9c761c7bf9b8e5510e3"; //todo remove from here

    private final TokenBanListRepository tokenBanListRepository;

    public String extractLogin(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Role extractRole(String token) {
        return extractClaim(token, claims -> {
            return Role.valueOf((String) claims.get("role"));
        });
    }

    public Long extractId(String token) {
        return Long.valueOf(extractClaim(token, Claims::getId));
    }

    public Optional<String> token(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        jwt = authHeader.substring("Bearer ".length());
        return Optional.of(jwt);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public void banToken(String token) {
        tokenBanListRepository.save(new BannedToken(token));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Optional<BannedToken> bannedToken = tokenBanListRepository.findById(token);
        if (bannedToken.isPresent()) {
            if (isTokenExpired(token))
                tokenBanListRepository.delete(bannedToken.get());

            return false;
        }


        final String username = extractLogin(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(Account account) {
        return generateToken(new HashMap<>(), account);
    }

    public String generateToken(Map<String, Object> extraClaims, Account account) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setId(String.valueOf(account.getId()))
                .setSubject(account.getUsername())
                .addClaims(Map.of("role", account.getRole()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 10000000)) //todo move to app.prop
                .signWith(getSingInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSingInKey())
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }

    private Key getSingInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public ResponseEntity<?> accessUser(HttpServletRequest request, Function<Long, ResponseEntity<?>> userConsumer) {
        Optional<String> jwt = token(request);
        if (jwt.isPresent()) {
            Long userId = extractId(jwt.get());

            try {
                return userConsumer.apply(userId);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.toString());
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
