package pl.kurs.config.security.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.kurs.config.security.model.AppUser;
import pl.kurs.config.security.model.Role;

public interface AppUserService {

    AppUser saveUser(AppUser user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    AppUser getUser(String username);
    Page<AppUser> getUsers(Pageable pageable);

}
