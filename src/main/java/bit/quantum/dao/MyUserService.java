package bit.quantum.dao;

import bit.quantum.entity.MyUser;
import bit.quantum.exception.UseNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class MyUserService {

    @Autowired
    private MyUserRepository repo;

    public MyUser findUser(String username) {
        Optional<MyUser> user = repo.findById(username);
        if (!user.isPresent()) {
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

        if( repo.updateSecretByUsername(username,secret)>0)
            return true;
        return false;
    }
}
