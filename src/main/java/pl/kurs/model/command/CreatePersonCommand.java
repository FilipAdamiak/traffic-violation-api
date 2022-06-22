package pl.kurs.model.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.kurs.validation.annotation.UniqueEmail;
import pl.kurs.validation.annotation.UniquePesel;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class CreatePersonCommand {

    @UniquePesel
    private String pesel;
    @NotEmpty(message = "NAME_NOT_EMPTY")
    private String name;
    @NotEmpty(message = "SURNAME_NOT_EMPTY")
    private String surname;
    @UniqueEmail
    private String email;
    @NotNull(message = "VISIT_CONFIRMED_EMPTY")
    private boolean isLicenseSuspended;

}
