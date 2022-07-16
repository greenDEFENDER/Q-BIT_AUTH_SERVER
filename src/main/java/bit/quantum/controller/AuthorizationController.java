package bit.quantum.controller;

import bit.quantum.dao.MyUserService;
import bit.quantum.entity.MyUser;
import bit.quantum.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthorizationController {
    @Autowired
    private MyUserService repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register a new user with authorization server.
    @PostMapping("/save")
    public ResponseEntity<MyUser> save(@RequestBody MyUser user) {
        user.setSecret(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var saveduser = repo.save(user);
        return new ResponseEntity<MyUser>(saveduser, HttpStatus.CREATED);
    }

    // Exchange user credentials for json web token.
    @PostMapping("/auth")
    public ResponseEntity<String> authenticate(HttpServletRequest request, HttpServletResponse response) {
        //getting username and password from header.
        String username = request.getHeader("username");
        String password = request.getHeader("password");

        return AuthenticationUtil.authenticate(username, password, response);

//        //validating that there are value in credentials
//        if (!isCredentialAcceptabel(username, password))
//            return new ResponseEntity<>("invalid credentials", HttpStatus.BAD_REQUEST);
//
//        // getting user from database.
//        MyUser user = repo.findUser(username);
//
//        // validating user credentials.
//        if (!passwordEncoder.matches(password, user.getPassword()))
//            return new ResponseEntity<>("Invalid credentials", HttpStatus.BAD_REQUEST);
//
//        // generate secret.
//        String secret = user.generateSecret();
//        repo.updateSecret(username, secret);
//
//        // Building a json web token.
//        String jwt = buildToken(user, secret);
//
//        // adding token to response header.
//        response.setHeader("Authorization", jwt);
//
//        return new ResponseEntity<>("authenticated", HttpStatus.ACCEPTED);
    }


    // find secret of user.
    @GetMapping("/secret/{username}")
    public String secret(@PathVariable String username) {
        return repo.getSecret(username);
    }

    // revoke token
    @GetMapping("/revokeToken")
    public String revokeToken(HttpServletRequest request) {
        return AuthenticationUtil.revokeToken(request.getHeader("Authorization"));

        //  String jwt = request.getHeader("Authorization");
        // String username = getUserNameFromJwt(jwt);
        // String secret = repo.getSecret(username);
        // if (!validateToken(jwt, secret))
//            return "token already invalid";
//        repo.updateSecret("invalid", username);
//        return "token invalidated";
    }

//    private boolean validateToken(String jwt, String secret) {
//        try {
//            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(jwt)
//                    .getBody();
//        } catch (Exception e) {
//            return false;
//        }
//        return true;
//    }
//

//    //get username from jwt token.
//    private String getUserNameFromJwt(String jwt) {
//        int i = jwt.lastIndexOf('.');
//        String jwtWithoutSignature = jwt.substring(0, i + 1);
//
//        // @formatter:off
//        Claims untrusted = Jwts.parserBuilder()
//                .build()
//                .parseClaimsJwt(jwtWithoutSignature)
//                .getBody();
//        // @formatter:on
//
//        return untrusted.get("username").toString();
//    }

//    private boolean isCredentialAcceptabel(String username, String password) {
//        return (username != null && !username.isEmpty()) && (password != null && !password.isEmpty());
//    }

//    private String buildToken(MyUser user, String secret) {
//        //creating signing key based on user secret.
//        SecretKey signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

//        return Jwts.builder()
//                .addClaims(Map.of("username", user.getUsername()))
//                .addClaims(Map.of("authorities", user.getRoles()))
//                .setIssuedAt(Date.from(Instant.now()))
//                //.setExpiration()
//                .signWith(signingKey)
//                .compact();
//    }

}
