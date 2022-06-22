package pl.kurs.errors.constraints;

public interface ConstraintErrorHandler {
    String constraintName();

    String message();

    String field();
}
