package pl.kurs.validation.impl;

import org.springframework.stereotype.Service;
import pl.kurs.model.entity.Violation;
import pl.kurs.model.enums.ViolationType;
import pl.kurs.validation.annotation.PaymentValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
public class PaymentValidator implements ConstraintValidator<PaymentValid, Violation> {

    @Override
    public boolean isValid(Violation violation, ConstraintValidatorContext constraintValidatorContext) {
        ViolationType type = violation.getType();
        return violation.getPayment() >= type.getMinPayment() && violation.getPayment() <= type.getMaxPayment();
    }
}
