package pl.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import pl.kurs.model.entity.TrafficViolation;

import java.time.LocalDateTime;
import java.util.List;

public interface ViolationRepository extends JpaRepository<TrafficViolation, Integer>, QuerydslPredicateExecutor<TrafficViolation> {

    List<TrafficViolation> findAllByPerson_PeselAndDateAfter(String pesel, LocalDateTime date);
}
