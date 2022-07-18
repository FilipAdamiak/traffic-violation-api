package pl.kurs.model.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.kurs.validation.annotation.OneOfValues;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CreateTicketCommand {

    @PastOrPresent(message = "DATE_FROM_FUTURE")
    private LocalDateTime date;
    @Pattern(regexp = "\\d{11}", message = "PESEL_NOT_VALID")
    private String personPesel;
    @OneOfValues(values = {"AB", "CD", "CS", "CW", "CH"})
    private String series;
    @NotNull(message = "TICKET_PAYED_NOT_NULL")
    private boolean payed;
}
