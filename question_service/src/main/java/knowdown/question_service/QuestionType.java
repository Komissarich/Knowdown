package knowdown.question_service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum QuestionType {
    MULTIPLE_CHOICE;
    // TRUE_FALSE;


    @JsonCreator
    public static QuestionType fromString(String value) {
        if (value == null) return null;
        return switch (value.toUpperCase()) {
            case "MULTIPLE_CHOICE"   -> MULTIPLE_CHOICE;
            // case "TRUE_FALSE" -> "";
          
            default -> MULTIPLE_CHOICE;
            // default -> throw new IllegalArgumentException(
            //     "Invalid type: " + value + ". Allowed values:  MULTIPLE_CHOICE, TRUE_FALSE"
            // );
        };
    }

    @JsonValue
    public String toString() {
        return name(); 
    }
}
