package pl.kurs.config.security.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.config.security.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
