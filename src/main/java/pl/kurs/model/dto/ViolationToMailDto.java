package pl.kurs.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ViolationToMailDto {

    private String personData;
    private String date;
    private int points;
    private int payment;
    private String type;
    private int totalPoints;
}
