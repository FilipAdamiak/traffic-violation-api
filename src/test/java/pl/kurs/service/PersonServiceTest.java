package pl.kurs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.kurs.model.command.CreatePersonCommand;
import pl.kurs.model.command.UpdatePersonCommand;
import pl.kurs.model.entity.Person;
import pl.kurs.repository.PersonRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
                .pesel("01292009610")
                .name("Adam")
                .surname("Graczyk")
                .email("moderntoking7@gmail.com")
                .isLicenseSuspended(false)
                .build();
    }

    @Test
    void shouldAddPerson() {
        CreatePersonCommand command = CreatePersonCommand.builder()
                .pesel("01292009610")
                .name("Adam")
                .surname("Graczyk")
                .email("moderntoking7@gmail.com")
                .isLicenseSuspended(false)
                .build();
        Mockito.when(personRepository.saveAndFlush(person1))
                .thenReturn(person1);
        personService.createPerson(command);
        Mockito.verify(personRepository).saveAndFlush(person1);
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
                .isLicenseSuspended(true)
                .version(0)
                .build();
        Person afterUpdate = Person.builder()
                .pesel("01292009610")
                .name("Michal")
                .surname("Pawelec")
                .email("moderntoking7@gmail.com")
                .isLicenseSuspended(true)
                .build();
        Mockito.when(personRepository.saveAndFlush(person1))
                .thenReturn(afterUpdate);
        Person updated = personService.editPerson(person1, command);
        Mockito.verify(personRepository).saveAndFlush(person1);
        assertTrue(updated.isLicenseSuspended());
    }




}