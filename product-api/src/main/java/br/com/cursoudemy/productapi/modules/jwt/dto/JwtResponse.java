package br.com.cursoudemy.productapi.modules.jwt.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse implements Serializable {

    private Integer id;
    private String name;
    private String email;

    public static JwtResponse getUser(Claims jwtClaims){
      try {
          return new ObjectMapper().convertValue(jwtClaims.get("authUser"), JwtResponse.class);
         /* return JwtResponse
                  .builder()
                  .id((Integer) jwtClaims.get("id"))
                  .name((String) jwtClaims.get("name"))
                  .email((String) jwtClaims.get("email"))
                  .build();
          */
      } catch (Exception ex){
          return null;
      }
    }
}
