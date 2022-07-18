package pl.kurs.config.security.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.config.security.model.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);

    @Query(value = "select distinct u from AppUser u left join fetch u.roles",
            countQuery = "select count(u) from AppUser u")
    Page<AppUser> findAllWithRoles(Pageable pageable);
}
