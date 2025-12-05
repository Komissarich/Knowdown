package knowdown.question_service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum QuestionDifficulty {
    EASY,
    MEDIUM,
    HARD;


    @JsonCreator
    public static QuestionDifficulty fromString(String value) {
        if (value == null) return null;
        return switch (value.toUpperCase()) {
            case "EASY"   -> EASY;
            case "MEDIUM" -> MEDIUM;
            case "HARD"   -> HARD;
            default -> throw new IllegalArgumentException(
                "Invalid difficulty: " + value + ". Allowed values: EASY, MEDIUM, HARD"
            );
        };
    }

    @JsonValue
    public String toString() {
        return name(); 
    }
}
