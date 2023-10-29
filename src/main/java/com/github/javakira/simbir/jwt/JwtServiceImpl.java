package com.github.javakira.simbir.jwt;

import com.github.javakira.simbir.account.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:jwt.properties")
public class JwtServiceImpl implements JwtService {
    private final JwtProperties properties;
    private final TokenBanListRepository tokenBanListRepository;

    @Override
    public String extractLogin(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public long extractId(String token) {
        return Long.parseLong(extractClaim(token, Claims::getId));
    }

    @Override
    public Optional<String> token(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        jwt = authHeader.substring("Bearer ".length());
        return Optional.of(jwt);
    }

    @Override
    public void banToken(String token) {
        tokenBanListRepository.save(new BannedToken(token));
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        Optional<BannedToken> bannedToken = tokenBanListRepository.findById(token);
        if (bannedToken.isPresent()) {
            if (isTokenExpired(token))
                tokenBanListRepository.delete(bannedToken.get()); //todo маловероятно что про токен не забудут и он удалится вообще когда нибудь

            return false;
        }


        final String username = extractLogin(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    @Override
    public String generateToken(Account account) {
        return generateToken(new HashMap<>(), account);
    }

    @Override
    public ResponseEntity<?> accessUser(HttpServletRequest request, Function<Long, ResponseEntity<?>> userConsumer) {
        Optional<String> jwt = token(request);
        if (jwt.isPresent()) {
            long userId = extractId(jwt.get());

            try {
                return userConsumer.apply(userId);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.toString());
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(Map<String, Object> extraClaims, Account account) {
        return Jwts.builder()
                .claims()
                .empty()
                .add(extraClaims)
                .subject(account.getUsername())
                .add(Map.of("role", account.getRole()))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + properties.getExpiration()))
                .id(String.valueOf(account.getId()))
                .and()
                .signWith(getSingInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSingInKey())
                .decryptWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSingInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
