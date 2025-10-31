package knowdown.question_service.dto;

import knowdown.question_service.QuestionCategory;
import knowdown.question_service.QuestionDifficulty;
import knowdown.question_service.QuestionType;

import java.util.List;

public record QuestionResponse(
        String question,
        QuestionDifficulty difficulty,
        QuestionType type,
        String category,
        String correctAnswer,
        List<String> incorrectAnswers
) {}
