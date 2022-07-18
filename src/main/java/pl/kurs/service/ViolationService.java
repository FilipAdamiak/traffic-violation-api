package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.errors.EntityNotFoundException;
import pl.kurs.errors.ViolationAlreadyExistsException;
import pl.kurs.model.command.CreateViolationCommand;
import pl.kurs.model.command.UpdateViolationCommand;
import pl.kurs.model.entity.Violation;
import pl.kurs.model.searchcriteria.SearchViolationCriteria;
import pl.kurs.repository.ViolationRepository;

@Service
@RequiredArgsConstructor
public class ViolationService {

    private final ViolationRepository violationRepository;

    @Transactional
    public Page<Violation> findWithPredicate(Pageable pageable, SearchViolationCriteria criteria) {
        return violationRepository.findAll(criteria.toPredicate(), pageable);
    }

    @Transactional
    public void deleteById(int id) {
        violationRepository.findById(id).ifPresent(violationRepository::delete);
    }

    @Transactional(readOnly = true)
    public Page<Violation> findAll(Pageable pageable) {
        return violationRepository.findAll(pageable);
    }

    @Transactional
    public Violation edit(Violation violation, UpdateViolationCommand command) {
        violation.setPoints(command.getPoints());
        violation.setPayment(command.getPayment());
        violation.setType(command.getType());
        if(violationRepository.findByTypeAndPointsAndPayment(violation.getType(), violation.getPoints(), violation.getPayment()).isPresent()) {
            throw new ViolationAlreadyExistsException(violation.getType(), violation.getPayment(), violation.getPoints());
        }
        return violationRepository.saveAndFlush(violation);
    }

    @Transactional
    public Violation createViolation(CreateViolationCommand command) {
        if (violationRepository.findByTypeAndPointsAndPayment(command.getType(), command.getPoints(), command.getPayment()).isPresent()) {
            throw new ViolationAlreadyExistsException(command.getType(), command.getPayment(), command.getPoints());
        }

        return violationRepository.saveAndFlush(Violation.builder()
                .payment(command.getPayment())
                .points(command.getPoints())
                .type(command.getType())
                .build());
    }

    @Transactional
    public Violation findById(int id) {
        return violationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Violation", String.valueOf(id)));
    }

}
