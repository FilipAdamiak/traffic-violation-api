package pl.kurs.model.searchcriteria;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import pl.kurs.model.entity.QTrafficViolation;
import pl.kurs.model.enums.ViolationType;
import pl.kurs.validation.annotation.OneOfViolationType;

import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
public class SearchViolationCriteria {

    @PastOrPresent(message = "DATE_FROM_FUTURE")
    private LocalDateTime dateFrom;
    @PastOrPresent(message = "DATE_FROM_FUTURE")
    private LocalDateTime dateTo;
    @Range(min = 0, max = 15, message = "SEARCH_POINTS_OUT_OF_RANGE")
    private int pointsFrom;
    @Range(min = 0, max = 15, message = "SEARCH_POINTS_OUT_OF_RANGE")
    private int pointsTo;
    @Range(min = 0, max = 5000, message = "SEARCH_PAYMENT_OUT_OF_RANGE")
    private int paymentFrom;
    @Range(min = 0, max = 5000, message = "SEARCH_PAYMENT_OUT_OF_RANGE")
    private int paymentTo;
    @OneOfViolationType
    private ViolationType type;

    @Builder
    public SearchViolationCriteria(LocalDateTime dateFrom, LocalDateTime dateTo, int pointsFrom, int pointsTo, int paymentFrom, int paymentTo, ViolationType type) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.pointsFrom = pointsFrom;
        this.pointsTo = pointsTo;
        this.paymentFrom = paymentFrom;
        this.paymentTo = paymentTo;
        this.type = type;
    }

    public Predicate toPredicate() {
        BooleanBuilder builder = new BooleanBuilder();
        Optional.of(getPointsFrom())
                .map(QTrafficViolation.trafficViolation.points::goe).ifPresent(builder::and);
        Optional.of(getPointsTo())
                .map(QTrafficViolation.trafficViolation.points::loe).ifPresent(builder::and);
        Optional.of(getPaymentFrom())
                .map(QTrafficViolation.trafficViolation.payment::goe).ifPresent(builder::and);
        Optional.of(getPaymentTo())
                .map(QTrafficViolation.trafficViolation.payment::loe).ifPresent(builder::and);
        Optional.of(getDateFrom())
                .map(QTrafficViolation.trafficViolation.date::after).ifPresent(builder::and);
        Optional.of(getDateTo())
                .map(QTrafficViolation.trafficViolation.date::before).ifPresent(builder::and);
        Optional.of(getType())
                .map(QTrafficViolation.trafficViolation.type::eq).ifPresent(builder::and);
        return builder;
    }

}
