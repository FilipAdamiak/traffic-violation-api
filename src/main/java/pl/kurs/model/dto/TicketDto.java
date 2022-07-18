package pl.kurs.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class TicketDto extends RepresentationModel<TicketDto> {

    private int id;
    private String personPesel;
    private LocalDateTime date;
    private boolean deleted;
    private boolean payed;
    private String series;
    private int version;
    private int totalPayment;
    private int totalPoints;
}
