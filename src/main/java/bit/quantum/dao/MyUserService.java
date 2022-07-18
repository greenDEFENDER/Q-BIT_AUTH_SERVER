package bit.quantum.dao;

import bit.quantum.entity.MyUser;
import bit.quantum.exception.UseNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

@Service
@Transactional
public class MyUserService {

    @Autowired
    private MyUserRepository repo;

    public MyUser findUser(String username) {
        Optional<MyUser> user = repo.findById(username);
        if (user.isEmpty()) {
            throw new UseNotFoundException("user does not exist");
        }
        return user.get();
    }

    public MyUser save(MyUser user) {
        return repo.save(user);
    }

    public String getSecret(String username) {
        return repo.getSecretByUsername(username);
    }

    public boolean updateSecret(String username, String secret){

        return repo.updateSecretByUsername(username, secret) > 0;
    }

}
