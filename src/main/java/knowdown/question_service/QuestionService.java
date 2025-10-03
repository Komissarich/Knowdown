package knowdown.question_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import knowdown.question_service.questionApi.ApiQuestion;
import knowdown.question_service.questionApi.QuestionApiResponse;
import knowdown.question_service.translationApi.TranslationApiRequest;
import knowdown.question_service.translationApi.TranslationApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectSerializer;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        QuestionApiResponse QuestionApiResponse = restTemplate.getForObject(builder.toUriString(), QuestionApiResponse.class);
        return QuestionApiResponseToQuestion(QuestionApiResponse);
    }


    private Question QuestionApiResponseToQuestion(QuestionApiResponse QuestionApiResponse) {
        if (QuestionApiResponse.response_code() != 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "External API error");
        }

        ApiQuestion apiQuestion = QuestionApiResponse.results().getFirst();

        String question = translateString(apiQuestion.question());
        QuestionDifficulty difficulty = Objects.equals(apiQuestion.difficulty(), "easy") ? QuestionDifficulty.EASY :
                Objects.equals(apiQuestion.difficulty(), "medium") ? QuestionDifficulty.MEDIUM :
                                        QuestionDifficulty.HARD;
        QuestionType type = Objects.equals(apiQuestion.type(), "multiple") ? QuestionType.MULTIPLE_CHOICE :
                            QuestionType.TRUE_FALSE;

        String correctAnswer;
        List<String> incorrectAnswers = new ArrayList<String>();
        if (type == QuestionType.TRUE_FALSE) {
            correctAnswer = "Правда";
            incorrectAnswers.add("Ложь");
        } else {
            correctAnswer = translateString(apiQuestion.correct_answer());
            apiQuestion.incorrect_answers().forEach((answer) -> {
                incorrectAnswers.add(translateString(answer));
            });
        }

        return new Question(
                question,
                difficulty,
                type,
                correctAnswer,
                incorrectAnswers
        );
    }


    public String translateString(String ruString) {
        log.info("translateString called with string " + ruString);
        String url = "http://127.0.0.1:5000/translate";
        TranslationApiRequest request = new TranslationApiRequest(ruString, "en", "ru");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TranslationApiRequest> entity = new HttpEntity<TranslationApiRequest>(request, headers);
        TranslationApiResponse apiResponse = restTemplate.postForObject(url, entity, TranslationApiResponse.class);
        log.info("translate api response: " + apiResponse.toString());
        return apiResponse.translatedText();
    }
}
