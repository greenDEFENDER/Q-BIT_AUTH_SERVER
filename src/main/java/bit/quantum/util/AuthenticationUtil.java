package bit.quantum.util;

import bit.quantum.dao.MyUserService;
import bit.quantum.entity.MyUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class AuthenticationUtil {

    private static MyUserService repo;
    private static PasswordEncoder passwordEncoder;

    @Autowired
    private void setRepo(MyUserService repo) {
        AuthenticationUtil.repo = repo;
    }

    @Autowired
    private void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        AuthenticationUtil.passwordEncoder = passwordEncoder;
    }

    public static ResponseEntity<String> authenticate(String username, String password, HttpServletResponse response) {

        //validating that there are value in credentials
        if (!isCredentialAcceptable(username, password))
            return new ResponseEntity<>("invalid credentials", HttpStatus.BAD_REQUEST);

        // getting user from database.
        MyUser user = repo.findUser(username);

        // validating user credentials.
        if (!passwordEncoder.matches(password, user.getPassword()))
            return new ResponseEntity<>("Invalid credentials", HttpStatus.BAD_REQUEST);

        // generate secret.
        String secret = user.generateSecret();
        repo.updateSecret(username, secret);

        // Building a json web token.
        String jwt = buildToken(user, secret);

        // adding token to response header.
        response.setHeader("Authorization", jwt);

        return new ResponseEntity<>(jwt, HttpStatus.ACCEPTED);
    }

    private static String buildToken(MyUser user, String secret) {
        //creating signing key based on user secret.
        SecretKey signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .addClaims(Map.of("username", user.getUsername()))
                .addClaims(Map.of("authorities", user.getRoles()))
                .setIssuedAt(Date.from(Instant.now()))
                //.setExpiration()
                .signWith(signingKey)
                .compact();
    }

    public static String revokeToken(String token) {

        if (token == null || token.isEmpty())
            return "token already invalid";

        String username = getUserNameFromJwt(token);
        String secret = repo.getSecret(username);
        if (!validateToken(token, secret))
            return "token already invalid";
        repo.updateSecret("invalid", username);
        return "token invalidated";
    }

    public static String encode(String password) {
        return passwordEncoder.encode(password);
    }

    private static boolean validateToken(String jwt, String secret) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    //get username from jwt token.
    private static String getUserNameFromJwt(String jwt) {
        int i = jwt.lastIndexOf('.');
        String jwtWithoutSignature = jwt.substring(0, i + 1);
        Claims untrusted = null;
        try {
            // @formatter:off
            untrusted = Jwts.parserBuilder()
                    .build()
                    .parseClaimsJwt(jwtWithoutSignature)
                    .getBody();
            // @formatter:on
        } catch (Exception e) {
            return "invalid";
        }
        return untrusted.get("username").toString();
    }

    private static boolean isCredentialAcceptable(String username, String password) {
        return (username != null && !username.isEmpty()) && (password != null && !password.isEmpty());
    }


}
