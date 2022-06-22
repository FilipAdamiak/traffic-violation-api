package pl.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.config.security.model.AuthenticationRequest;

import java.util.Optional;

public interface AuthenticationRepository extends JpaRepository<AuthenticationRequest, Integer> {

    @Query("select u from AuthenticationRequest u left join fetch u.roles where u.username = ?1")
    Optional<AuthenticationRequest> findByUsernameWithRoles(String username);
}
