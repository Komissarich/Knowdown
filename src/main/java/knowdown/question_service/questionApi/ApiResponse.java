package knowdown.question_service.questionApi;

import java.util.List;

public record ApiResponse (
    Integer response_code,
    List<ApiQuestion> results

) {}
