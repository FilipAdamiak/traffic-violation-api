package pl.kurs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.kurs.model.command.CreateViolationCommand;
import pl.kurs.model.command.UpdateViolationCommand;
import pl.kurs.model.entity.Violation;
import pl.kurs.model.enums.ViolationType;
import pl.kurs.repository.ViolationRepository;

import java.util.Optional;

class ViolationServiceTest {

    @InjectMocks
    private ViolationService violationService;
    @Mock
    private ViolationRepository violationRepository;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        violationService = new ViolationService(violationRepository);
    }

    @Test
    void shouldAddViolation() {
        Violation violation = new Violation(10, ViolationType.COLLISION, 1500);
        CreateViolationCommand command = CreateViolationCommand.builder()
                .type(ViolationType.COLLISION)
                .payment(1500)
                .points(10)
                .build();
        Mockito.when(violationRepository.saveAndFlush(violation))
                .thenReturn(violation);
        violationService.createViolation(command);
        Mockito.verify(violationRepository).findByTypeAndPointsAndPayment(command.getType(), command.getPoints(), command.getPayment());
        Mockito.verify(violationRepository).saveAndFlush(violation);
    }

    @Test
    void shouldDeleteViolation() {
        Violation violation = new Violation(10, ViolationType.COLLISION, 1500);
        Mockito.when(violationRepository.findById(1))
                .thenReturn(Optional.of(violation));
        violationService.deleteById(1);
        Mockito.verify(violationRepository).findById(1);
        Mockito.verify(violationRepository).delete(violation);
    }

    @Test
    void shouldEditViolation() {
        Violation violation = new Violation(10, ViolationType.COLLISION, 1500);
        UpdateViolationCommand command = UpdateViolationCommand.builder()
                .type(ViolationType.TECHNICAL_CONDITION)
                .payment(3000)
                .points(8)
                .build();
        Violation afterUpdate = new Violation(8, ViolationType.TECHNICAL_CONDITION, 3000);
        Mockito.when(violationRepository.saveAndFlush(afterUpdate))
                .thenReturn(afterUpdate);
        violationService.edit(violation, command);
        Mockito.verify(violationRepository).findByTypeAndPointsAndPayment(command.getType(), command.getPoints(), command.getPayment());
    }

}
