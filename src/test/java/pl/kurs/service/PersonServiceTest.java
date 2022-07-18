package pl.kurs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.kurs.model.command.CreatePersonCommand;
import pl.kurs.model.command.UpdatePersonCommand;
import pl.kurs.model.entity.Person;
import pl.kurs.repository.PersonRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonServiceTest {

    @InjectMocks
    private PersonService personService;
    @Mock
    private PersonRepository personRepository;

    private Person person1;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        personService = new PersonService(personRepository);
        person1 = Person.builder()
                .pesel("03292009610")
                .name("Adam")
                .surname("Graczyk")
                .email("moderntoking7@gmail.com")
                .licenseSuspended(false)
                .build();
    }

    @Test
    void shouldAddPerson() {
        Person person = Person.builder()
                .pesel("03292009610")
                .name("Adam")
                .surname("Graczyk")
                .email("moderntoking7@gmail.com")
                .licenseSuspended(false)
                .build();
        CreatePersonCommand command = CreatePersonCommand.builder()
                .pesel("03292009610")
                .name("Adam")
                .surname("Graczyk")
                .email("moderntoking7@gmail.com")
                .licenseSuspended(false)
                .build();
        Mockito.when(personRepository.saveAndFlush(person))
                .thenReturn(person);
        personService.createPerson(command);
        Mockito.verify(personRepository).saveAndFlush(person);
    }

    @Test
    void shouldSoftDeletePerson() {
        Mockito.when(personRepository.findById(1))
                .thenReturn(Optional.ofNullable(person1));
        Person person = personService.softDelete(1);
        Mockito.verify(personRepository).findById(1);
        assertTrue(person.isDeleted());
    }

    @Test
    void shouldEditPerson() {
        UpdatePersonCommand command = UpdatePersonCommand.builder()
                .name("Michal")
                .surname("Pawelec")
                .email("moderntoking7@gmail.com")
                .licenseSuspended(true)
                .version(0)
                .build();
        Person afterUpdate = Person.builder()
                .pesel("03292009610")
                .name("Michal")
                .surname("Pawelec")
                .email("moderntoking7@gmail.com")
                .licenseSuspended(true)
                .build();
        Mockito.when(personRepository.saveAndFlush(person1))
                .thenReturn(afterUpdate);
        Person updated = personService.editPerson(person1, command);
        Mockito.verify(personRepository).saveAndFlush(person1);
        assertTrue(updated.isLicenseSuspended());
    }




}