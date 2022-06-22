package pl.kurs.validation.impl;

import org.springframework.stereotype.Service;
import pl.kurs.model.enums.ViolationType;
import pl.kurs.validation.annotation.OneOfViolationType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.EnumSet;
import java.util.Set;

@Service
public class OneOfViolationValidator implements ConstraintValidator<OneOfViolationType, ViolationType> {

    private final Set<ViolationType> types = EnumSet.allOf(ViolationType.class);

    @Override
    public boolean isValid(ViolationType type, ConstraintValidatorContext constraintValidatorContext) {
        return types.contains(type);
    }
}
