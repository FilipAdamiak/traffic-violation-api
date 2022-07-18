package pl.kurs.mappings;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import pl.kurs.controller.PersonController;
import pl.kurs.model.dto.TicketDto;
import pl.kurs.model.entity.Ticket;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class TicketToDtoMapper implements Converter<Ticket, TicketDto> {

    @Override
    public TicketDto convert(MappingContext<Ticket, TicketDto> mappingContext) {
        Ticket ticket = mappingContext.getSource();

        TicketDto ticketDto = TicketDto.builder()
                .id(ticket.getId())
                .date(ticket.getDate())
                .deleted(ticket.isDeleted())
                .personPesel(ticket.getPerson().getPesel())
                .version(ticket.getVersion())
                .series(ticket.getSeries())
                .payed(ticket.isPayed())
                .totalPayment(ticket.getTotalPayment())
                .totalPoints(ticket.getTotalPoints())
                .build();

        ticketDto.add(linkTo(methodOn(PersonController.class).getSinglePerson(ticket.getPerson().getId())).withRel("person-details"));
        return ticketDto;
    }
}
