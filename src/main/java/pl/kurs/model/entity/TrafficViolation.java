package pl.kurs.model.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import pl.kurs.model.enums.ViolationType;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = "person")
@SQLDelete(sql = "UPDATE traffic_violation SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@ToString(exclude = {"person", "id"})
public class TrafficViolation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime date;
    private int points;
    private int payment;
    private boolean deleted;
    @Enumerated(EnumType.STRING)
    private ViolationType type;
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;
    @Version
    private int version;

    @Builder
    public TrafficViolation(LocalDateTime date, int points, int payment, Person person, ViolationType type) {
        this.date = date;
        this.points = points;
        this.payment = payment;
        this.person = person;
        this.deleted = false;
        this.type = type;
        person.addViolation(this);
    }

    public TrafficViolation(){}


}
