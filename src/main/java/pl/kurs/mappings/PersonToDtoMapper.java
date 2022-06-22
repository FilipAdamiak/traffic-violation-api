package pl.kurs.mappings;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import pl.kurs.controller.PersonController;
import pl.kurs.model.dto.PersonDto;
import pl.kurs.model.entity.Person;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Service
public class PersonToDtoMapper implements Converter<Person, PersonDto> {

    @Override
    public PersonDto convert(MappingContext<Person, PersonDto> mappingContext) {
        Person person = mappingContext.getSource();

        PersonDto personDto = PersonDto.builder()
                .id(person.getId())
                .name(person.getName())
                .surname(person.getSurname())
                .email(person.getEmail())
                .isLicenseSuspended(person.isLicenseSuspended())
                .version(person.getVersion())
                .pesel(person.getPesel())
                .deleted(person.isDeleted())
                .build();

        personDto.add(linkTo(methodOn(PersonController.class).getPersonViolations(personDto.getId())).withRel("person-violations"));
        return personDto;
    }
}
