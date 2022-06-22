package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.errors.EntityNotFoundException;
import pl.kurs.model.command.CreatePersonCommand;
import pl.kurs.model.command.UpdatePersonCommand;
import pl.kurs.model.entity.Person;
import pl.kurs.model.entity.TrafficViolation;
import pl.kurs.repository.PersonRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    @Transactional(readOnly = true)
    public Page<Person> findAll(Pageable pageable) {
        return personRepository.findAll(pageable);
    }

    @Transactional
    public Person createPerson(CreatePersonCommand command) {
        return personRepository.saveAndFlush(Person.builder()
                .name(command.getName())
                .surname(command.getSurname())
                .email(command.getEmail())
                .pesel(command.getPesel())
                .isLicenseSuspended(command.isLicenseSuspended())
                .build());
    }

    @Transactional
    public Person findById(int id) {
        return personRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Person", String.valueOf(id)));
    }

    @Transactional
    public Person softDelete(int id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person", String.valueOf(id)));
        if(!person.isDeleted()) {
            person.setDeleted(true);
        }
        return person;
    }

    @Transactional
    public Person editPerson(Person person, UpdatePersonCommand command) {
        person.setEmail(command.getEmail());
        person.setName(command.getName());
        person.setSurname(command.getSurname());
        person.setLicenseSuspended(command.isLicenseSuspended());
        person.setVersion(command.getVersion());
        return personRepository.saveAndFlush(person);
    }



}
