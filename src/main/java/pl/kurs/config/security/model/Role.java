package pl.kurs.config.security.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    private String name;

    public Role(String name) {
        this.name = name;
    }

    public Role(){}

    @Override
    public String getAuthority() {
        return name;
    }
}