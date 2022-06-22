package pl.kurs.service;

import freemarker.template.TemplateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.kurs.mail.EmailService;
import pl.kurs.model.command.CreateViolationCommand;
import pl.kurs.model.entity.Person;
import pl.kurs.model.entity.TrafficViolation;
import pl.kurs.model.enums.ViolationType;
import pl.kurs.repository.PersonRepository;
import pl.kurs.repository.ViolationRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ViolationServiceTest {

    @InjectMocks
    private ViolationService violationService;
    @Mock
    private ViolationRepository violationRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private EmailService emailService;
    private TrafficViolation trafficViolation;
    private Person person;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        violationService = new ViolationService(violationRepository, personRepository, emailService);
        person = Person.builder()
                .pesel("01292009610")
                .name("Adam")
                .surname("Graczyk")
                .email("moderntoking7@gmail.com")
                .isLicenseSuspended(false)
                .build();
        trafficViolation = TrafficViolation.builder()
                .date(LocalDateTime.of(2022, 6, 19, 20, 0, 0))
                .payment(1500)
                .points(10)
                .person(person)
                .type(ViolationType.COLLISION)
                .build();
    }

    @Test
    void shouldAddTrafficViolation() throws TemplateException, IOException {
        CreateViolationCommand command = CreateViolationCommand.builder()
                .date(trafficViolation.getDate())
                .payment(1500)
                .points(10)
                .personPesel("01292009610")
                .type(ViolationType.COLLISION)
                .build();
        Mockito.when(personRepository.findByPesel("01292009610"))
                .thenReturn(Optional.ofNullable(person));
        Mockito.when(violationRepository.saveAndFlush(trafficViolation))
                .thenReturn(trafficViolation);
        violationService.createViolation(command);
        Mockito.verify(violationRepository).saveAndFlush(trafficViolation);
    }

    @Test
    void shouldSoftDeleteTrafficViolation() {
        Mockito.when(violationRepository.findById(1))
                .thenReturn(Optional.ofNullable(trafficViolation));
        TrafficViolation violation = violationService.softDelete(1);
        Mockito.verify(violationRepository).findById(1);
        assertTrue(violation.isDeleted());
    }

}