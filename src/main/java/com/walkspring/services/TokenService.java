package com.walkspring.services;

import com.walkspring.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class TokenService {

    @Value("${jwt.token.secret}")
    private String jwtSecret;

    @Value("${jwt.token.prefix}")
    private String jwtPreFix;

    public String generateTokenWithClaims(User user) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("Role", user.getUserRole().toString());
        return generateToken(claims, user);
    }

    //Hier wird der Token dann ertsellt als String die Claims können auch ausgelassen werden siehe Beschreibung bei Methode generateTokenWithClaims()
    public String generateToken(Map<String, Object> claims, User user){
        return Jwts.builder()
                .setClaims(claims)
                //Subject = wem wurde Token ausgestellt
                .setSubject(user.getUsername())
                //Wann wurde ausgestellt
                .setIssuedAt(new Date(System.currentTimeMillis()))
                //Wann läuft er ab
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 *24))
                //Wie wird verschlüsstelt
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }


    //Diese Methode liest den Subject Claim aus also den Usernamen
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    //Holt einen bestimmten Claim aus dem Token
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    //Claims sind die Properties die im JWT gesetzt werden das beinhaltet sowohl die default claims als auch alle eigenen extra Claims
    //Holt aus dem token alle Claims raus
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    //Beschreibt nach welchen Algorithmus verschlüsselt werden soll und welches Secret zum Decodieren verwendet wird
    private Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Überprüft ob der Token zum User gehört und ob er expired ist.
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);


        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiredAt(token).before(new Date(System.currentTimeMillis()));
    }

    private Date extractExpiredAt(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
