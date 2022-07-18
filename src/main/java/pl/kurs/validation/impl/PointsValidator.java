package pl.kurs.validation.impl;

import org.springframework.stereotype.Service;
import pl.kurs.model.entity.Violation;
import pl.kurs.model.enums.ViolationType;
import pl.kurs.validation.annotation.PointsValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
public class PointsValidator implements ConstraintValidator<PointsValid, Violation> {

    @Override
    public boolean isValid(Violation violation, ConstraintValidatorContext constraintValidatorContext) {
        ViolationType type = violation.getType();
        return violation.getPoints() >= type.getMinPoints() && violation.getPoints() <= type.getMaxPoints();
    }
}
