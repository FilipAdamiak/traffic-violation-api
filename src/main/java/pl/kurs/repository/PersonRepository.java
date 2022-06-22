package pl.kurs.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.model.entity.Person;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Integer> {

    boolean existsByPesel(String pesel);

    boolean existsByEmail(String email);

    Optional<Person> findByPesel(String pesel);

    @Query("select distinct p from Person p left join fetch p.trafficViolations where p.id = ?1")
    Optional<Person> findById(int id);

}
