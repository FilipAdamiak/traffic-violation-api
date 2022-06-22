package pl.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.config.security.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("select u from User u left join fetch u.roles where u.username = ?1")
    Optional<User> findByUsernameWithRoles(String username);
}
