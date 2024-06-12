package com.sigei.ms_auth_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "EFdrSorzMeI05vZd/0m3MJUvPjMaCxRYf/Nx9Jv5HKp5gzM//7Z6W3diRURRK8jTGsQYwx9/wRcl+9uloCrl+5U3k0yFg5Uxzz4xlqJrEulPvs90bJSWdX+HfHnke22lU5UWzgpDZIp2tRAD8JE7FCNaS+Wzu2Fnr1YccQxcWwnuu+tfYv7BLA+QXhj1O2vk9jc14rq+89oPM+oxy0aGMYpc6HnEqxx+fVdS/iK4oM9kb8uyAQK1N1WhUmWTF4Bo7dkK8dLIcbHgzguuwMu6FKur081VDDnoayuiN25rBHulp5lNCx1GJWnpngY00Lc4MQ6tblukcExfH8ECuQmJ1Z0b33va6p/Y4i2hACB3VQI=\n";
    public String extractUsername(String token) {
        System.out.println("extractUsername");
        return extractClaim(token, Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        System.out.println("extractClaim");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }


    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        System.out.println("This is username" + username);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private Claims extractAllClaims(String token){
        System.out.println("Claims");
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
