package pl.kurs.service;

import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.errors.EntityNotFoundException;
import pl.kurs.errors.TooManyPointsInPreviousYearException;
import pl.kurs.mail.EmailService;
import pl.kurs.model.command.CreateViolationCommand;
import pl.kurs.model.command.UpdateViolationCommand;
import pl.kurs.model.entity.TrafficViolation;
import pl.kurs.repository.PersonRepository;
import pl.kurs.repository.ViolationRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ViolationService {

    private final ViolationRepository violationRepository;
    private final PersonRepository personRepository;
    private final EmailService emailService;


    @Transactional(readOnly = true)
    public Page<TrafficViolation> findAll(Pageable pageable) {
        return violationRepository.findAll(pageable);
    }

    @Transactional
    public TrafficViolation createViolation(CreateViolationCommand command) throws TemplateException, IOException {
        List<TrafficViolation> criteriaViolations = violationRepository.findAllByPerson_PeselAndDateAfter(command.getPersonPesel(),
                LocalDateTime.now().minusYears(1));
        //calculate all points from prev year
        int points = criteriaViolations.stream().mapToInt(TrafficViolation::getPoints).sum();

        //if points are greater than 24 won't add new traffic violation
        if(points > 24) {
            throw new TooManyPointsInPreviousYearException("Too many points already", points, command.getPersonPesel());
        }

        TrafficViolation violation = TrafficViolation.builder()
                .payment(command.getPayment())
                .date(command.getDate())
                .points(command.getPoints())
                .type(command.getType())
                .person(personRepository.findByPesel(command.getPersonPesel())
                        .orElseThrow(() -> new EntityNotFoundException("Person", command.getPersonPesel())))
                .build();

        //if sum of new points and actual are greater than 24 - send email and suspend license
        if(points + violation.getPoints() > 24) {
            emailService.sendMail(violation);
            violation.getPerson().setLicenseSuspended(true);
        }
        return violationRepository.saveAndFlush(violation);
    }

    @Transactional
    public TrafficViolation softDelete(int id) {
        TrafficViolation violation = violationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Violation", String.valueOf(id)));
        if(!violation.isDeleted()) {
            violation.setDeleted(true);
        }
        return violation;
    }

    @Transactional
    public TrafficViolation edit(TrafficViolation violation, UpdateViolationCommand command) {
        violation.setDate(command.getDate());
        violation.setPayment(command.getPayment());
        violation.setVersion(command.getVersion());
        violation.setPerson(personRepository.findByPesel(command.getPersonPesel())
                .orElseThrow(() -> new EntityNotFoundException("Person", command.getPersonPesel())));
        violation.setPoints(command.getPoints());
        violation.setType(command.getType());
        return violationRepository.saveAndFlush(violation);
    }

    @Transactional
    public TrafficViolation findById(int id) {
        return violationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Person", String.valueOf(id)));
    }
}
