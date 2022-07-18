package pl.kurs.mappings;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import pl.kurs.model.dto.TicketToMailDto;
import pl.kurs.model.entity.Ticket;

import java.time.format.DateTimeFormatter;

@Service
public class TicketToMailMapper implements Converter<Ticket, TicketToMailDto> {

    @Override
    public TicketToMailDto convert(MappingContext<Ticket, TicketToMailDto> mappingContext) {
        Ticket ticket = mappingContext.getSource();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return TicketToMailDto.builder()
                .date(ticket.getDate().format(formatter))
                .personData(ticket.getPerson().toString())
                .totalPoints(ticket.getTotalPoints())
                .totalPayment(ticket.getTotalPayment())
                .build();
    }
}
