package pl.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.model.entity.TrafficViolation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ViolationRepository extends JpaRepository<TrafficViolation, Integer> {

    List<TrafficViolation> findAllByPerson_PeselAndDateAfter(String pesel, LocalDateTime date);
}
