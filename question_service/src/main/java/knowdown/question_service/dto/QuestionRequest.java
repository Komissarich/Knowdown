package knowdown.question_service.dto;

import knowdown.question_service.QuestionCategory;
import knowdown.question_service.QuestionDifficulty;
import knowdown.question_service.QuestionType;

public record QuestionRequest (
        QuestionDifficulty difficulty,
        QuestionType type,
        QuestionCategory category
) {

}
