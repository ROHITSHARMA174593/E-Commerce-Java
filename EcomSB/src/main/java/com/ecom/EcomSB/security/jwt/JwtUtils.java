package com.ecom.EcomSB.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtExpirationMs}")
    private long jwtExpirationInMs;
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    // Getting JWT From Headers
    public String getJwtFromHeader(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        logger.debug("Authorization Header : {}", bearerToken);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7); // postman me hum kuch aise bhejenge header me "Bearer <>" to Bearer.length = 6 and 1 space and then our token start isliye 7 likha hai ki uske baad ka data do
        }
        return null;
    }

    // Generating Tokens from UserName
    public String generateTokenFromUserName(UserDetails userDetails){
        String userName = userDetails.getUsername();
        return Jwts.builder()
                .subject(userName).issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + jwtExpirationInMs)))
                .signWith(key())
                .compact();
    }

    // Getting UserName from JWT Tokens
    public String getUserNameFromJWTTOken(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    // Generate Signin Key
    public Key key(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    // Valid JWT Token
    public boolean validateJWTToken(String authToken){
        try {
            System.out.println("Validate");
            Jwts.parser().verifyWith((SecretKey) key())
                    .build().parseSignedClaims(authToken);
            return true;
        }catch (MalformedJwtException err){
            logger.error("Invalid JWT Token: {}", err.getMessage());
        }catch (ExpiredJwtException err){
            logger.error("JWT Token is Expired : {}", err.getMessage());
        }catch (UnsupportedJwtException err){
            logger.error("JWT Token is UnSupported : {}", err.getMessage());
        }catch (IllegalArgumentException err){
            logger.error("JWT claims string is empty : {}", err.getMessage());
        }
        return false;
    }
}
