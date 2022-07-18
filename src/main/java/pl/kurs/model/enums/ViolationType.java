package pl.kurs.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ViolationType {

    HIGH_SPEED("High speed", 50, 5000, 0, 15),
    DRIFTING("Drifting", 3000, 5000, 4, 8),
    COLLISION("Collision", 1000, 2500, 6, 12),
    LIGHTS("Lights", 300, 1000, 4, 8),
    TECHNICAL_CONDITION("Technical condition", 2000, 4000, 6, 10),
    RED_LIGHT("Red light", 500, 1000, 10, 14);

    private final String label;
    private final int minPayment;
    private final int maxPayment;
    private final int minPoints;
    private final int maxPoints;

    ViolationType(String label, int minPayment, int maxPayment, int minPoints, int maxPoints) {
        this.label = label;
        this.minPayment = minPayment;
        this.maxPayment = maxPayment;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

}
