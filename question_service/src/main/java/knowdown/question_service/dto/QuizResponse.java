package knowdown.question_service.dto;

import java.util.List;

public record QuizResponse (
    List<QuestionResponse> quiz_questions
) { }
