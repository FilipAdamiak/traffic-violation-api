package pl.kurs.validation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kurs.repository.PersonRepository;
import pl.kurs.validation.annotation.UniquePesel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
@RequiredArgsConstructor
public class UniquePeselValidator implements ConstraintValidator<UniquePesel, String> {

    private final PersonRepository personRepository;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return  (!personRepository.existsByPesel(s)) && (s.length() == 11);
    }
}
