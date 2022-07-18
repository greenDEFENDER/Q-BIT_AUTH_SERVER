package bit.quantum.controller;

import bit.quantum.dao.MyUserService;
import bit.quantum.dto.TokenDTO;
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
        return new ResponseEntity<>(saveduser, HttpStatus.CREATED);
    }

    // Exchange user credentials for json web token.
    @PostMapping("/auth")
    public ResponseEntity<Object> authenticate(HttpServletRequest request, HttpServletResponse response) {
        //getting username and password from header.
        String username = request.getHeader("username");
        String password = request.getHeader("password");

        return AuthenticationUtil.authenticate(username, password, response);
    }

    // find secret of user.
    @GetMapping("/secret/{username}")
    public String secret(@PathVariable String username) {
        return repo.getSecret(username);
    }

    // revoke token
    @GetMapping("/revokeToken")
    public String revokeToken(@RequestBody TokenDTO tokenDTO) {
        return AuthenticationUtil.revokeToken(tokenDTO.getAccessToken());
    }


    //handling runtime exception.
    @ExceptionHandler
    ResponseEntity<Object> handleException(Exception e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
