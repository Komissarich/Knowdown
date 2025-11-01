package knowdown.question_service.questionApi;

import java.util.List;

public record QuestionApiResponse (
    Integer response_code,
    List<ApiQuestion> results
) {}
