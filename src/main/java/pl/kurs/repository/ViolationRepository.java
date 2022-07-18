package pl.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import pl.kurs.model.entity.Violation;
import pl.kurs.model.enums.ViolationType;

import java.util.Optional;

public interface ViolationRepository extends JpaRepository<Violation, Integer>, QuerydslPredicateExecutor<Violation> {


    @Query("select distinct v from Violation v where v.type = ?1 and v.points = ?2 and v.payment = ?3")
    Optional<Violation> findByTypeAndPointsAndPayment(ViolationType type, int points, int payment);

}
