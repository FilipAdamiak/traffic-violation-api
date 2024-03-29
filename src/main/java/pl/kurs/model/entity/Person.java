package pl.kurs.model.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@EqualsAndHashCode(exclude = "tickets")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "pesel", name = "UC_PERSON_PESEL"), @UniqueConstraint(columnNames = "email", name = "UC_PERSON_EMAIL")})
@SQLDelete(sql = "UPDATE person SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String pesel;
    private String name;
    private String surname;
    private String email;
    private boolean deleted;
    private boolean licenseSuspended;
    @OneToMany(mappedBy = "person", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<Ticket> tickets = new HashSet<>();
    @Version
    private int version;

    @Builder
    public Person(String pesel, String name, String surname, String email, boolean licenseSuspended) {
        this.pesel = pesel;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.licenseSuspended = licenseSuspended;
        this.deleted = false;
    }

    public Person(){}

    @Override
    public String toString() {
        return "Mr/Mrs " + name + " " + surname;
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }
}
