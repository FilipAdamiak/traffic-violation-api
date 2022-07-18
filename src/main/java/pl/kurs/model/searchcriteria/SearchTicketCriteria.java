package pl.kurs.model.searchcriteria;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.kurs.model.entity.QTicket;
import pl.kurs.validation.annotation.OneOfValues;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
public class SearchTicketCriteria {

    @PastOrPresent(message = "DATE_FROM_FUTURE")
    private LocalDateTime dateFrom;
    @PastOrPresent(message = "DATE_FROM_FUTURE")
    private LocalDateTime dateTo;
    @OneOfValues(values = {"AB", "CD", "CS", "CW", "CH"})
    private String series;
    @NotNull(message = "PAYED_FIELDS_NOT_NULL")
    private boolean payed;
    @NotEmpty(message = "PERSON_PESEL_NOT_EMPTY")
    private String personPesel;

    @Builder
    public SearchTicketCriteria(LocalDateTime dateFrom, LocalDateTime dateTo, String series, boolean payed, String personPesel) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.series = series;
        this.payed = payed;
        this.personPesel = personPesel;
    }

    public Predicate toPredicate() {
        BooleanBuilder builder = new BooleanBuilder();
        Optional.of(getDateFrom())
                .map(QTicket.ticket.date::after).ifPresent(builder::and);
        Optional.of(getDateTo())
                .map(QTicket.ticket.date::before).ifPresent(builder::and);
        Optional.of(getSeries())
                .map(QTicket.ticket.series::eq).ifPresent(builder::and);
        Optional.of(isPayed())
                .map(QTicket.ticket.payed::eq).ifPresent(builder::and);
        Optional.of(getPersonPesel())
                .map(QTicket.ticket.person.pesel::eq).ifPresent(builder::and);
        return builder;
    }

}
