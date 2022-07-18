package pl.kurs.validation.annotation;

import pl.kurs.validation.impl.PaymentValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PaymentValidator.class)
public @interface PaymentValid {
    String message() default "VIOLATION_PAYMENT_BESIDE_RANGE";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
