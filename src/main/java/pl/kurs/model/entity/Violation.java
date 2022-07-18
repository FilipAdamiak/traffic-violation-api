package pl.kurs.model.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.kurs.model.enums.ViolationType;
import pl.kurs.validation.annotation.PaymentValid;
import pl.kurs.validation.annotation.PointsValid;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = "ticket")
@PaymentValid
@PointsValid
public class Violation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int points;
    private int payment;
    @Enumerated(EnumType.STRING)
    private ViolationType type;
    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Builder
    public Violation(int points, ViolationType type, int payment) {
        this.points = points;
        this.type = type;
        this.payment = payment;
    }

    public Violation() {}

}
