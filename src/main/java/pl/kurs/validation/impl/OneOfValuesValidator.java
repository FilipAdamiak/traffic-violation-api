package pl.kurs.validation.impl;

import org.springframework.stereotype.Service;
import pl.kurs.validation.annotation.OneOfValues;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

@Service
public class OneOfValuesValidator implements ConstraintValidator<OneOfValues, String> {

    private Set<String> allowedValues;

    @Override
    public void initialize(OneOfValues constraintAnnotation) {
        this.allowedValues = Set.of(constraintAnnotation.values());
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return allowedValues.contains(s);
    }
}
