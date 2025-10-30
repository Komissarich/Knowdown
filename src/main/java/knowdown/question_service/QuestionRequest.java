package knowdown.question_service;

public record QuestionRequest (
        QuestionDifficulty difficulty,
        QuestionType type
) {

}
