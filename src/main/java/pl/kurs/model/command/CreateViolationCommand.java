package pl.kurs.model.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.kurs.model.enums.ViolationType;
import pl.kurs.validation.annotation.OneOfViolationType;

@Getter
@Setter
@Builder
public class CreateViolationCommand {

    private Integer points;
    private Integer payment;
    @OneOfViolationType
    private ViolationType type;

}
