package pl.kurs.model.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.kurs.model.enums.ViolationType;
import pl.kurs.validation.annotation.OneOfViolationType;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CreateViolationCommand {

    @PastOrPresent(message = "DATE_FROM_FUTURE")
    private LocalDateTime date;
    @Min(value = 0, message = "POINTS_NOT_NEGATIVE")
    @Max(value = 15, message = "TOO_MANY_POINTS")
    private int points;
    @Min(value = 50, message = "PAYMENT_UNDER_RANGE")
    @Max(value = 5000, message = "PAYMENT_TOO_HIGH")
    private int payment;
    @OneOfViolationType
    private ViolationType type;
    @NotEmpty(message = "PESEL_NOT_EMPTY")
    private String personPesel;

}
