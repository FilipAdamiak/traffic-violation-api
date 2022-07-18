package pl.kurs.model.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.kurs.validation.annotation.UniqueEmail;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class UpdatePersonCommand {

    @NotEmpty(message = "NAME_NOT_EMPTY")
    private String name;
    @NotEmpty(message = "SURNAME_NOT_EMPTY")
    private String surname;
    @UniqueEmail
    private String email;
    @NotNull(message = "LICENSE_SUSPENDED_NOT_NULL")
    private boolean licenseSuspended;
    @NotNull(message = "VERSION_NOT_EMPTY")
    private Integer version;
}
