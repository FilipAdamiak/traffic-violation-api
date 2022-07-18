package pl.kurs.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketToMailDto {

    private String personData;
    private String date;
    private int totalPayment;
    private int totalPoints;

}
