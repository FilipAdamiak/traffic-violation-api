package pl.kurs.validation.annotation;

import pl.kurs.validation.impl.OneOfValuesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OneOfValuesValidator.class)
public @interface OneOfValues {
    String message() default "VALUE_NOT_ALLOWED";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] values();
}
