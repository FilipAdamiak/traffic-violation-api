package pl.kurs.validation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.repository.PersonRepository;
import pl.kurs.validation.annotation.UniquePesel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UniquePeselValidator implements ConstraintValidator<UniquePesel, String> {

    private final PersonRepository personRepository;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        Pattern pattern = Pattern.compile("\\d{11}");
        Matcher matcher = pattern.matcher(s);
        return  (!personRepository.existsByPesel(s)) && matcher.matches();
    }
}
