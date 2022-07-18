package pl.kurs.errors;

import lombok.EqualsAndHashCode;
import lombok.Value;
import pl.kurs.model.enums.ViolationType;

@EqualsAndHashCode(callSuper = true)
@Value
public class ViolationAlreadyExistsException extends RuntimeException{
    ViolationType violationType;
    int payment;
    int points;
}

