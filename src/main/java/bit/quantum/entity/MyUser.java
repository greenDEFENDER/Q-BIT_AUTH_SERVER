package bit.quantum.entity;


import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Set;
import java.util.UUID;

@Entity
public class MyUser {

    @Id
    private String username;

    private String password;

    private String secret;

    @ElementCollection
    private Set<String> roles;

    public MyUser(String username, String password, Set<String> roles, String secret) {
        super();
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String generateSecret() {
        return UUID.randomUUID().toString();
    }

    protected MyUser() {
        super();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}

