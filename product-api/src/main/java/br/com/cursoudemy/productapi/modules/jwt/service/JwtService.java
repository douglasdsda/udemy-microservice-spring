package br.com.cursoudemy.productapi.modules.jwt.service;

import br.com.cursoudemy.productapi.config.exceptions.AuthenticationException;
import br.com.cursoudemy.productapi.config.exceptions.ValidationException;
import br.com.cursoudemy.productapi.modules.jwt.dto.JwtResponse;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.util.Strings;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.springframework.util.ObjectUtils.isEmpty;


@Service
public class JwtService {

    private static final String EMPTY_SPACE = " ";
    private static final Integer TOKEN_INDEX = 1;
    @Value("${app-config.secrets.api-secret}")
    private String apiSecret;

    public void validateAuthorization(String token){
        var accessToken = extracToken(token);
        try {

           var claims = Jwts
                   .parserBuilder()
                   .setSigningKey(Keys.hmacShaKeyFor(apiSecret.getBytes()))
                   .build()
                   .parseClaimsJws(accessToken)
                   .getBody();


            var user = JwtResponse.getUser(claims);
            if (isEmpty(user) || isEmpty(user.getId())){
                throw new AuthenticationException("The user is not valid.");
            }
        }  catch (Exception ex) {
            ex.printStackTrace();

            if(ex.getMessage().contains("expired"))
                throw new AuthenticationException("The token is expired.");

            throw new AuthenticationException("Error white trying to process the Access Token");
        }
    }

    public String extracToken(String token){
         if(isEmpty(token)){
             throw new AuthenticationException("The access token was not informed.");
         }
         if(token.contains(EMPTY_SPACE)){
             token = token.split(EMPTY_SPACE)[TOKEN_INDEX];
         }
         return token;
    }

}
