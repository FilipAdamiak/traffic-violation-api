package pl.kurs.service;

import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.errors.EntityNotFoundException;
import pl.kurs.errors.TooManyPointsInPreviousYearException;
import pl.kurs.errors.ViolationAlreadyExistsException;
import pl.kurs.mail.EmailService;
import pl.kurs.model.command.CreateTicketCommand;
import pl.kurs.model.command.CreateViolationCommand;
import pl.kurs.model.command.UpdateTicketCommand;
import pl.kurs.model.entity.Ticket;
import pl.kurs.model.entity.Violation;
import pl.kurs.model.searchcriteria.SearchTicketCriteria;
import pl.kurs.repository.PersonRepository;
import pl.kurs.repository.TicketRepository;
import pl.kurs.repository.ViolationRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final PersonRepository personRepository;
    private final ViolationRepository violationRepository;
    private final EmailService emailService;

    @Transactional
    public Page<Ticket> findWithPredicate(Pageable pageable, SearchTicketCriteria criteria) {
        return ticketRepository.findAll(criteria.toPredicate(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<Ticket> findAll(Pageable pageable) {
        return ticketRepository.findAll(pageable);
    }

    @Transactional
    public Ticket createTicket(CreateTicketCommand command) {
        Ticket ticket = Ticket.builder()
                .series(command.getSeries())
                .date(command.getDate())
                .payed(command.isPayed())
                .person(personRepository.findByPesel(command.getPersonPesel())
                        .orElseThrow(() -> new EntityNotFoundException("Person", command.getPersonPesel())))
                .build();
        return ticketRepository.saveAndFlush(ticket);
    }

    @Transactional
    public Violation addViolationToTicket(int ticketId, CreateViolationCommand command) throws TemplateException, IOException {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket", String.valueOf(ticketId)));

        int points = ticket.getViolations().stream().mapToInt(Violation::getPoints).sum();

        if(points > 24) {
            throw new TooManyPointsInPreviousYearException("Too many points already", points, ticket.getPerson().getPesel());
        }

        Violation violation = violationRepository.findByTypeAndPointsAndPayment(command.getType(), command.getPoints(), command.getPayment())
                .orElse(violationRepository.saveAndFlush(Violation.builder()
                        .type(command.getType())
                        .payment(command.getPayment())
                        .points(command.getPoints())
                        .build()));

        if(ticket.getViolations().contains(violation)) {
            throw new ViolationAlreadyExistsException(violation.getType(), violation.getPayment(), violation.getPoints());
        }

        if(points + violation.getPoints() > 24) {
            emailService.sendMail(ticket, violation);
            ticket.getPerson().setLicenseSuspended(true);
        }

        ticket.addViolation(violation);
        return violation;
    }

    @Transactional
    public Ticket softDelete(int id) {
        Ticket violation = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket", String.valueOf(id)));
        if(!violation.isDeleted()) {
            violation.setDeleted(true);
        }
        return violation;
    }

    @Transactional
    public Ticket edit(Ticket ticket, UpdateTicketCommand command) {
        ticket.setVersion(command.getVersion());
        ticket.setPayed(command.isPayed());
        ticket.setPerson(personRepository.findByPesel(command.getPersonPesel())
                .orElseThrow(() -> new EntityNotFoundException("Person", command.getPersonPesel())));
        return ticketRepository.saveAndFlush(ticket);
    }

    @Transactional
    public Ticket findById(int id) {
        return ticketRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Person", String.valueOf(id)));
    }
}
