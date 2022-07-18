package pl.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import pl.kurs.model.entity.Ticket;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Integer>, QuerydslPredicateExecutor<Ticket> {

    @Query("select distinct t from Ticket t left join fetch t.violations where t.id = ?1")
    Optional<Ticket> findById(Integer id);

}
