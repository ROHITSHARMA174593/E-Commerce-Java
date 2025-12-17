package com.ecom.EcomSB.security.jwt;

import com.ecom.EcomSB.security.service.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    //todo : Ye jo data hai ye :: application.properties file se aa rha hai jaise react and node me process.env likhte hai to yaha per kuch aisa hota hai vo syntax
    @Value("${spring.app.jwtExpirationMs}")
    private long jwtExpirationInMs;
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.ecom.app.jwtCookieName}")
    private String jwtCookie;


    //todo :Now get JWT from Cookies
    public String getJwtFromCookies(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if(cookie != null){
//            System.out.println("Cookie : "+ cookie.getValue());
            return cookie.getValue();
        }else {
            return null;
        }
    }

    public String getJwtFromHeader(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal){
        String jwt = generateTokenFromUserName(userPrincipal.getUsername());
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt)
                .path("/api")
                .maxAge(24 * 60 * 60) // 1 day convert in seconds (our jwt cookie is expired in 1 day)
                .httpOnly(false)
                .build();
        return cookie;
    }
    public ResponseCookie getCleanJwtCookie(){
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, null)
                .path("/api")
                .build();
        return cookie;
    }


    // Generating Tokens from UserName
    public String generateTokenFromUserName(String userName){
//        String userName = userDetails.getUsername();
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

//    public String generateTokenFromUsername(UserDetails userDetails) {
//        String username = userDetails.getUsername();
//        return Jwts.builder()
//                .subject(username)
//                .issuedAt(new Date())
//                .expiration(new Date((new Date()).getTime() + jwtExpirationInMs))
//                .signWith(key())
//                .compact();
//    }

}
