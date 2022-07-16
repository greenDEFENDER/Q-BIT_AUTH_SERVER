package bit.quantum.dao;

import bit.quantum.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MyUserRepository extends JpaRepository<MyUser,String> {

    @Query(value ="SELECT SECRET FROM MY_USER U WHERE U.USERNAME=?1", nativeQuery = true)
    String getSecretByUsername(String username);

    @Modifying
    @Query(value = "UPDATE MY_USER U SET SECRET=?2 WHERE U.USERNAME=?1 ",nativeQuery = true)
    int updateSecretByUsername(String username, String secret);
}
