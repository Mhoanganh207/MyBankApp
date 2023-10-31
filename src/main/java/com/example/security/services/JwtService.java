package com.example.security.services;

import com.example.security.model.AccountDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {


    private final static String SECRET_KEY = "itboTqh2KFMmaMzhfzzRbWAzWwCFve6Vadye483b343";

    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String jwt) {
            return Jwts.parserBuilder().setSigningKey(getSigningKey()).
                    build().parseClaimsJws(jwt).getBody();
        }

    private Key getSigningKey() {
        byte[] secretBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(secretBytes);
    }

    public String generateToken(Map<String, Object> extraClaims, AccountDetails accountDetails) {
        return Jwts.builder().
                setClaims(extraClaims)
                .setSubject(accountDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256,getSigningKey())
                .compact();
    }


    public String generateToken(AccountDetails accountDetails) {
        return generateToken(Map.of(), accountDetails);
    }

    public boolean isValidToken(String jwt, UserDetails accountDetails) {
        final String username = extractUsername(jwt);
        return (username.equals(accountDetails.getUsername()));
    }

    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    private Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }

}
