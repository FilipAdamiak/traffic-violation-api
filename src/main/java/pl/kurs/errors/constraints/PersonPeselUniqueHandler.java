package pl.kurs.errors.constraints;

import org.springframework.stereotype.Service;

@Service
public class PersonPeselUniqueHandler implements ConstraintErrorHandler{

    @Override
    public String constraintName() {
        return "UC_PERSON_PESEL";
    }

    @Override
    public String message() {
        return "PESEL_NOT_UNIQUE";
    }

    @Override
    public String field() {
        return "pesel";
    }

}
