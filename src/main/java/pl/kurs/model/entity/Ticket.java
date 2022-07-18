package pl.kurs.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE ticket SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime date;
    private String series;
    private boolean deleted;
    private boolean payed;
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ticket", cascade = CascadeType.ALL)
    private Set<Violation> violations = new HashSet<>();
    @Version
    private int version;

    @Builder
    public Ticket(LocalDateTime date, Person person, String series, boolean payed) {
        this.date = date;
        this.person = person;
        this.series = series;
        this.deleted = false;
        this.payed = payed;
        person.addTicket(this);
    }

    public Ticket(){}

    public void addViolation(Violation violation) {
        violations.add(violation);
        violation.setTicket(this);
    }

    public int getTotalPoints() {
        return violations.stream().mapToInt(Violation::getPoints).sum();
    }

    public int getTotalPayment() {
        return violations.stream().mapToInt(Violation::getPayment).sum();
    }

}
