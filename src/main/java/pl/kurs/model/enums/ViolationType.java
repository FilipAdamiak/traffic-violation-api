package pl.kurs.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ViolationType {

    HIGH_SPEED("High speed"),
    DRIFTING("Drifting"),
    COLLISION("Collision"),
    LIGHTS("Lights"),
    TECHNICAL_CONDITION("Technical condition"),
    RED_LIGHT("Red light");

    private final String label;

    ViolationType(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }
}
