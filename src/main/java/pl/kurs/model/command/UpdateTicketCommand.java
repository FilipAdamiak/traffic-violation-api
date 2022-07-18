package pl.kurs.model.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Builder
public class UpdateTicketCommand {

    @Pattern(regexp = "\\d{11}", message = "PESEL_NOT_VALID")
    private String personPesel;
    @NotNull(message = "VERSION_NOT_EMPTY")
    private Integer version;
    @NotNull(message = "TICKET_PAYED_NOT_NULL")
    private boolean payed;

}
