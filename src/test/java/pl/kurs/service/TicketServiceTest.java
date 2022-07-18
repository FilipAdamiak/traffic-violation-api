package pl.kurs.service;

import freemarker.template.TemplateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.kurs.errors.ViolationAlreadyExistsException;
import pl.kurs.mail.EmailService;
import pl.kurs.model.command.CreateViolationCommand;
import pl.kurs.model.entity.Person;
import pl.kurs.model.entity.Ticket;
import pl.kurs.model.entity.Violation;
import pl.kurs.model.enums.ViolationType;
import pl.kurs.repository.PersonRepository;
import pl.kurs.repository.TicketRepository;
import pl.kurs.repository.ViolationRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TicketServiceTest {

    @InjectMocks
    private TicketService ticketService;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private ViolationRepository violationRepository;
    private Person person;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        ticketService = new TicketService(ticketRepository, personRepository, violationRepository, emailService);
        person = Person.builder()
                .pesel("03292009610")
                .name("Adam")
                .surname("Graczyk")
                .email("moderntoking7@gmail.com")
                .licenseSuspended(false)
                .build();
    }

    @Test
    void shouldAddViolationToTicket() throws TemplateException, IOException {
        Ticket ticket = Ticket.builder()
                .date(LocalDateTime.now().minusDays(1))
                .person(person)
                .series("AB")
                .payed(false)
                .build();
        CreateViolationCommand command = CreateViolationCommand.builder()
                .type(ViolationType.RED_LIGHT)
                .points(10)
                .payment(600)
                .build();
        Violation violation = Violation.builder()
                .type(ViolationType.RED_LIGHT)
                .points(10)
                .payment(600)
                .build();
        Mockito.when(ticketRepository.findById(1))
                .thenReturn(Optional.of(ticket));
        Mockito.when(violationRepository.findByTypeAndPointsAndPayment(command.getType(), command.getPoints(), command.getPayment()))
                .thenReturn(Optional.of(violation));
        ticketService.addViolationToTicket(1, command);
        assertTrue(ticket.getViolations().contains(violation));
    }

    @Test
    void shouldThrowViolationAlreadyExistsInTicket() throws TemplateException, IOException {
        Ticket ticket = Ticket.builder()
                .date(LocalDateTime.now().minusDays(1))
                .person(person)
                .series("AB")
                .payed(false)
                .build();
        CreateViolationCommand command = CreateViolationCommand.builder()
                .type(ViolationType.RED_LIGHT)
                .points(10)
                .payment(600)
                .build();
        Violation violation = Violation.builder()
                .type(ViolationType.RED_LIGHT)
                .points(10)
                .payment(600)
                .build();
        Mockito.when(ticketRepository.findById(1))
                .thenReturn(Optional.of(ticket));
        Mockito.when(violationRepository.findByTypeAndPointsAndPayment(command.getType(), command.getPoints(), command.getPayment()))
                .thenReturn(Optional.of(violation));
        ticketService.addViolationToTicket(1, command);
        assertTrue(ticket.getViolations().contains(violation));
        assertThrows(ViolationAlreadyExistsException.class, () -> ticketService.addViolationToTicket(1, command));
    }

    @Test
    void shouldSoftDeleteTrafficViolation() {
        Ticket ticket = Ticket.builder()
                .date(LocalDateTime.now().minusDays(1))
                .person(person)
                .series("AB")
                .payed(false)
                .build();
        Mockito.when(ticketRepository.findById(1))
                .thenReturn(Optional.of(ticket));
        Ticket deleted = ticketService.softDelete(1);
        Mockito.verify(ticketRepository).findById(1);
        assertTrue(deleted.isDeleted());
    }

}