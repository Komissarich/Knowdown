package knowdown.user_service.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import knowdown.user_service.domain.User;
import knowdown.user_service.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${token.signing.key}")
    private String jwtSigningKey;

    //Извлечение имени пользователя из токена
    @Override
    public String extractUsername(String token) {
        return extractClaim(token,Claims::getSubject);
    }
    //Извлечение данных из токена
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    //Генерация токена
    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("username", customUserDetails.getUsername());
        }
        return generateToken(claims, userDetails);
    }

    //Генерация токена
    @Override
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60*60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    //Проверка токена на валидность
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    //Проверка токена на просроченность
    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    //Извлечение даты истечения токена
    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    //Извлечение всех данных из токена
    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }
    //Получение ключа для подписи токена
    @Override
    public Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
