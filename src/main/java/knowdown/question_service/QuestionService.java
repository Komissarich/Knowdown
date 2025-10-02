package knowdown.question_service;

import knowdown.question_service.questionApi.ApiQuestion;
import knowdown.question_service.questionApi.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class QuestionService {
    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    private RestTemplate restTemplate;

    public Question getQuestionFromApi(
            QuestionType type,
            QuestionDifficulty difficulty
    ) {
        String url = "https://opentdb.com/api.php";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("amount", 1);

        if (type != null) {
            builder.queryParam("type", type == QuestionType.MULTIPLE_CHOICE ? "multiple" : "boolean");
        }
        if (difficulty != null) {
            builder.queryParam("difficulty", difficulty.toString().toLowerCase());
        }
        log.info("URL: " + builder.toUriString());
        ApiResponse apiResponse = restTemplate.getForObject(builder.toUriString(), ApiResponse.class);
        return apiResponseToQuestion(apiResponse);
    }


    private Question apiResponseToQuestion(ApiResponse apiResponse) {
        if (apiResponse.response_code() != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "External API error");
        }

        ApiQuestion apiQuestion = apiResponse.results().getFirst();

        QuestionDifficulty difficulty = apiQuestion.difficulty() == "easy" ? QuestionDifficulty.EASY :
                                        apiQuestion.difficulty() == "medium" ? QuestionDifficulty.MEDIUM :
                                        QuestionDifficulty.HARD;
        QuestionType type = apiQuestion.type() == "multiple" ? QuestionType.MULTIPLE_CHOICE :
                            QuestionType.TRUE_FALSE;

        return new Question(
                apiQuestion.question(),
                difficulty,
                type,
                apiQuestion.correct_answer(),
                apiQuestion.incorrect_answers()
        );
    }
}
