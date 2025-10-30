package knowdown.question_service.questionApi;

import java.util.List;

public record ApiQuestion(
        String type,
        String difficulty,
        String category,
        String question,
        String correct_answer,
        List<String> incorrect_answers
) {
}
