package pl.kurs.errors;

import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.BadCredentialsException;

@EqualsAndHashCode(callSuper = true)
public class IncorrectPasswordException extends BadCredentialsException {

    public IncorrectPasswordException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
