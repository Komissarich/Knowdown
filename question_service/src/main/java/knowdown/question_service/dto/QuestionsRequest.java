package knowdown.question_service.dto;

import knowdown.question_service.QuestionCategory;
import knowdown.question_service.QuestionDifficulty;
import knowdown.question_service.QuestionType;

import java.util.List;

public record QuestionsRequest (
        int amount,
        List<QuestionDifficulty> difficulties,
        List<QuestionType> types,
        List<QuestionCategory> categories
) {

}
