package pl.kurs.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.model.command.CreatePersonCommand;
import pl.kurs.model.command.UpdatePersonCommand;
import pl.kurs.model.dto.PersonDto;
import pl.kurs.model.dto.TicketDto;
import pl.kurs.model.entity.Person;
import pl.kurs.service.PersonService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/people")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<Page<PersonDto>> getAllPeople(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(personService.findAll(pageable)
                .map(person -> modelMapper.map(person, PersonDto.class)));
    }

    @GetMapping("/{id}/violations")
    public ResponseEntity<List<TicketDto>> getPersonViolations(@PathVariable int id) {
        return ResponseEntity.ok(personService.findById(id)
                .getTickets()
                .stream()
                .map(violation -> modelMapper.map(violation, TicketDto.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDto> getSinglePerson(@PathVariable int id) {
        Person person = personService.findById(id);
        return new ResponseEntity(modelMapper.map(person, PersonDto.class), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PersonDto> addPerson(@RequestBody @Valid CreatePersonCommand command) {
        Person toAdd = personService.createPerson(command);
        return new ResponseEntity(modelMapper.map(toAdd, PersonDto.class), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity softDeletePerson(@PathVariable int id) {
        personService.softDelete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDto> editPerson(@PathVariable int id, @RequestBody @Valid UpdatePersonCommand command) {
        Person person = personService.findById(id);
        Person toUpdate = personService.editPerson(person, command);
        return new ResponseEntity(modelMapper.map(toUpdate, PersonDto.class), HttpStatus.OK);
    }


}
