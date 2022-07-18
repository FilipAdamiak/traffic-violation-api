package pl.kurs.model.searchcriteria;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import pl.kurs.model.entity.QViolation;
import pl.kurs.model.enums.ViolationType;
import pl.kurs.validation.annotation.OneOfViolationType;

import java.util.Optional;

@Getter
@Setter
public class SearchViolationCriteria {


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
    public SearchViolationCriteria(int pointsFrom, int pointsTo, int paymentFrom, int paymentTo, ViolationType type) {
        this.pointsFrom = pointsFrom;
        this.pointsTo = pointsTo;
        this.paymentFrom = paymentFrom;
        this.paymentTo = paymentTo;
        this.type = type;
    }

    public Predicate toPredicate() {
        BooleanBuilder builder = new BooleanBuilder();
        Optional.of(getPointsFrom())
                .map(QViolation.violation.points::goe).ifPresent(builder::and);
        Optional.of(getPointsTo())
                .map(QViolation.violation.points::loe).ifPresent(builder::and);
        Optional.of(getPaymentFrom())
                .map(QViolation.violation.payment::goe).ifPresent(builder::and);
        Optional.of(getPaymentTo())
                .map(QViolation.violation.payment::loe).ifPresent(builder::and);
        Optional.of(getType())
                .map(QViolation.violation.type::eq).ifPresent(builder::and);
        return builder;
    }

}
