package pl.kurs.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.model.command.CreateViolationCommand;
import pl.kurs.model.command.UpdateViolationCommand;
import pl.kurs.model.dto.ViolationDto;
import pl.kurs.model.entity.Violation;
import pl.kurs.model.searchcriteria.SearchViolationCriteria;
import pl.kurs.service.ViolationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/violations")
@RequiredArgsConstructor
public class ViolationController {

    private final ViolationService violationService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<Page<ViolationDto>> getAllViolations(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(violationService.findAll(pageable)
                .map(violation -> modelMapper.map(violation, ViolationDto.class)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViolationDto> getSingleViolation(@PathVariable int id) {
        Violation violation = violationService.findById(id);
        return new ResponseEntity(modelMapper.map(violation, ViolationDto.class), HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<ViolationDto>> searchViolation(@PageableDefault Pageable pageable, @RequestBody @Valid SearchViolationCriteria search) {
        return ResponseEntity.ok(violationService.findWithPredicate(pageable, search)
                .map(s -> modelMapper.map(s, ViolationDto.class)));
    }

    @PostMapping
    public ResponseEntity<ViolationDto> addViolation(@RequestBody @Valid CreateViolationCommand command) {
        Violation toAdd = violationService.createViolation(command);
        return new ResponseEntity(modelMapper.map(toAdd, ViolationDto.class), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteViolation(@PathVariable int id) {
        violationService.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViolationDto> editViolation(@PathVariable int id, @RequestBody @Valid UpdateViolationCommand command) {
        Violation violation = violationService.findById(id);
        Violation edited = violationService.edit(violation, command);
        return new ResponseEntity(modelMapper.map(edited, ViolationDto.class), HttpStatus.OK);
    }

}
