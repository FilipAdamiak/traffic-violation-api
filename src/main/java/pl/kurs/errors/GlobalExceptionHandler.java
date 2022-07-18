package pl.kurs.errors;

import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.kurs.errors.constraints.ConstraintErrorHandler;
import pl.kurs.model.enums.ViolationType;

import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Map<String, ConstraintErrorHandler> constraintErrorHandlerMap;

    public GlobalExceptionHandler(Set<ConstraintErrorHandler> handlers) {
        this.constraintErrorHandlerMap = handlers.stream()
                .collect(Collectors.toMap(ConstraintErrorHandler::constraintName, Function.identity()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity(
                e.getConstraintViolations()
                        .stream()
                        .map(ce -> new ConstraintValidationError(e.getLocalizedMessage()))
                        .collect(Collectors.toList()),
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ResponseEntity(
                e.getFieldErrors()
                        .stream()
                        .map(ve -> new ValidationError(ve.getDefaultMessage(), ve.getField()))
                        .collect(Collectors.toList()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity(
                new NotFoundDto(e.getName(), e.getKey()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ViolationAlreadyExistsException.class)
    public ResponseEntity handleViolationAlreadyExistsException(ViolationAlreadyExistsException e) {
        return new ResponseEntity(
                new ViolationExistence(e.getViolationType(), e.getPayment(), e.getPoints()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity handleIncorrectPasswordException(IncorrectPasswordException e) {
        return new ResponseEntity(
                new BadCredentials(e.getMessage(), e.getCause()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TooManyPointsInPreviousYearException.class)
    public ResponseEntity handleTooManyPointsInPreviousYearException(TooManyPointsInPreviousYearException e) {
        return new ResponseEntity(
                new TooManyPoints(e.getMsg(), e.getPoints(), e.getPersonPesel()),
                HttpStatus.BAD_REQUEST);
    }

    @Value
    class TooManyPoints {
        String msg;
        int points;
        String personPesel;
    }

    @Value
    class BadCredentials {
        String message;
        Throwable throwable;
    }

    @Value
    class NotFoundDto {
        String entityName;
        String entityKey;
    }

    @Value
    class ViolationExistence {
        ViolationType violationType;
        int payment;
        int points;
    }

    @Value
    class ValidationError {
        String error;
        String field;
    }

    @Value
    class ConstraintValidationError {
        String message;
    }

}
