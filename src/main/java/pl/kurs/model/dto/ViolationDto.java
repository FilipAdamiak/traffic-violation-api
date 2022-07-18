package pl.kurs.model.dto;


import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;
import pl.kurs.model.enums.ViolationType;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class ViolationDto extends RepresentationModel<ViolationDto> {

    private int id;
    private int points;
    private int payment;
    private ViolationType type;

}
