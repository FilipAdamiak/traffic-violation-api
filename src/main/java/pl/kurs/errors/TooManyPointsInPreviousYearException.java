package pl.kurs.errors;

import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class TooManyPointsInPreviousYearException extends RuntimeException{
    String msg;
    int points;
    String personPesel;
}
