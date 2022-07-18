package pl.kurs.model.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.kurs.model.enums.ViolationType;
import pl.kurs.validation.annotation.OneOfViolationType;

@Getter
@Setter
@Builder
public class UpdateViolationCommand {

    private int points;
    @OneOfViolationType
    private ViolationType type;
    private Integer payment;
}
