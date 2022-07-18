package pl.kurs.controller;

import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.model.command.CreateTicketCommand;
import pl.kurs.model.command.CreateViolationCommand;
import pl.kurs.model.command.UpdateTicketCommand;
import pl.kurs.model.dto.TicketDto;
import pl.kurs.model.dto.ViolationDto;
import pl.kurs.model.entity.Ticket;
import pl.kurs.model.entity.Violation;
import pl.kurs.model.searchcriteria.SearchTicketCriteria;
import pl.kurs.service.TicketService;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<Page<TicketDto>> getAllTickets(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(ticketService.findAll(pageable)
                .map(ticket -> modelMapper.map(ticket, TicketDto.class)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDto> getSingleTicket(@PathVariable int id) {
        Ticket violation = ticketService.findById(id);
        return new ResponseEntity(modelMapper.map(violation, TicketDto.class), HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<TicketDto>> searchTicket(@PageableDefault Pageable pageable, @RequestBody @Valid SearchTicketCriteria search) {
        return ResponseEntity.ok(ticketService.findWithPredicate(pageable, search)
                .map(s -> modelMapper.map(s, TicketDto.class)));
    }

    @PostMapping
    public ResponseEntity<TicketDto> addTicket(@RequestBody @Valid CreateTicketCommand command) {
        Ticket toAdd = ticketService.createTicket(command);
        return new ResponseEntity(modelMapper.map(toAdd, TicketDto.class), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/violations")
    public ResponseEntity addViolation(@RequestBody @Valid CreateViolationCommand command, @PathVariable int id) throws TemplateException, IOException {
        Violation violation = ticketService.addViolationToTicket(id, command);
        return new ResponseEntity(modelMapper.map(violation, ViolationDto.class), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity softDeleteTicket(@PathVariable int id) {
        ticketService.softDelete(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketDto> editTicket(@PathVariable int id, @RequestBody @Valid UpdateTicketCommand command) {
        Ticket violation = ticketService.findById(id);
        Ticket edited = ticketService.edit(violation, command);
        return new ResponseEntity(modelMapper.map(edited, TicketDto.class), HttpStatus.OK);
    }

}
