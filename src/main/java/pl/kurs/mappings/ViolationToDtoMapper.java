package pl.kurs.mappings;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import pl.kurs.controller.PersonController;
import pl.kurs.model.dto.ViolationDto;
import pl.kurs.model.entity.TrafficViolation;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ViolationToDtoMapper implements Converter<TrafficViolation, ViolationDto> {

    @Override
    public ViolationDto convert(MappingContext<TrafficViolation, ViolationDto> mappingContext) {
        TrafficViolation violation = mappingContext.getSource();

        ViolationDto violationDto = ViolationDto.builder()
                .id(violation.getId())
                .date(violation.getDate())
                .deleted(violation.isDeleted())
                .type(violation.getType())
                .payment(violation.getPayment())
                .personPesel(violation.getPerson().getPesel())
                .points(violation.getPoints())
                .version(violation.getVersion())
                .build();

        violationDto.add(linkTo(methodOn(PersonController.class).getSinglePerson(violation.getPerson().getId())).withRel("person-details"));
        return violationDto;
    }
}
