package in.amalamama.authify.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret.key") //we wanna read the value from application.properties file
    private  String SECRET_KEY;
    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims=new HashMap<>();
        return createToken(claims,userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String email) {
     return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*10))
                .signWith(SignatureAlgorithm.HS256,SECRET_KEY)
             .compact();
    }
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
    public <T> T extractClaims(String token, Function<Claims,T> claimsResolver){
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String extractEmail(String token){
        return extractClaims(token,Claims::getSubject);//look to the top you gonna see that we set the subject as the email.
    }
    public Date extractExpiration(String token){
        return extractClaims(token,Claims::getExpiration);

    }
    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    //should be not expired and contain the email of the logged user
    public Boolean validateToken(String token,UserDetails userDetails){
        String email=extractEmail(token);
        return !isTokenExpired(token) && userDetails.getUsername().equals(email);
    };
}
