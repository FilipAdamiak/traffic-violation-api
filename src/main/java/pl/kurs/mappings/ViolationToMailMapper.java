package pl.kurs.mappings;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import pl.kurs.model.dto.ViolationToMailDto;
import pl.kurs.model.entity.TrafficViolation;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class ViolationToMailMapper implements Converter<TrafficViolation, ViolationToMailDto> {

    @Override
    public ViolationToMailDto convert(MappingContext<TrafficViolation, ViolationToMailDto> mappingContext) {
        TrafficViolation violation = mappingContext.getSource();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return ViolationToMailDto.builder()
                .date(violation.getDate().format(formatter))
                .type(violation.getType().toString().toLowerCase(Locale.ROOT))
                .payment(violation.getPayment())
                .personData(violation.getPerson().toString())
                .points(violation.getPoints())
                .totalPoints(violation.getPerson()
                        .getTrafficViolations()
                        .stream()
                        .mapToInt(TrafficViolation::getPoints)
                        .sum())
                .build();
    }
}
