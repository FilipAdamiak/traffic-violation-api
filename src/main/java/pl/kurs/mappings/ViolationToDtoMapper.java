package pl.kurs.mappings;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import pl.kurs.controller.TicketController;
import pl.kurs.model.dto.ViolationDto;
import pl.kurs.model.entity.Violation;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ViolationToDtoMapper implements Converter<Violation, ViolationDto> {

    @Override
    public ViolationDto convert(MappingContext<Violation, ViolationDto> mappingContext) {
        Violation violation = mappingContext.getSource();

        ViolationDto violationDto = ViolationDto.builder()
                .id(violation.getId())
                .type(violation.getType())
                .points(violation.getPoints())
                .payment(violation.getPayment())
                .build();
        violationDto.add(linkTo(methodOn(TicketController.class).getSingleTicket(violation.getTicket().getId())).withRel("ticket-details"));
        return violationDto;
    }
}
