package pl.kurs.validation.annotation;



import pl.kurs.validation.impl.UniqueEmailValidator;
import pl.kurs.validation.impl.UniquePeselValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmail {

    String message() default "EMAIL_NOT_UNIQUE_OR_INVALID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
