package knowdown.question_service;

import java.util.List;

public record Question (
        String question,
        QuestionDifficulty difficulty,
        QuestionType type,
        String correctAnswer,
        List<String> incorrectAnswers
) {}
