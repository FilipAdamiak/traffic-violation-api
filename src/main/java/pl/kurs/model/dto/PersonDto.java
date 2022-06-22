package pl.kurs.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class PersonDto extends RepresentationModel<PersonDto> {

    private int id;
    private String name;
    private String surname;
    private String pesel;
    private String email;
    private int version;
    private boolean isLicenseSuspended;
    private boolean deleted;

}
