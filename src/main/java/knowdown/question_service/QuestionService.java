package knowdown.question_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import knowdown.question_service.database.QuestionEntity;
import knowdown.question_service.database.QuestionRepository;
import knowdown.question_service.dto.QuestionResponse;
import knowdown.question_service.questionApi.ApiQuestion;
import knowdown.question_service.questionApi.QuestionApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
public class QuestionService {
    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    private final RestTemplate restTemplate;
    private final QuestionRepository repository;

    public QuestionService(QuestionRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        log.info("Init method called");

        final int needed = 200;
        if (repository.count() < needed) {
            String token = getAPISessionToken();
            log.info("Token: " + token);

            int counter = 0;
            while (counter < needed) {
                List<ApiQuestion> apiQuestions = getQuestionsFromApi(50, token);
                if (apiQuestions == null) {
                    break;
                }
                for (ApiQuestion q: apiQuestions) {
                    QuestionDifficulty difficulty = QuestionDifficulty.valueOf(q.difficulty().toUpperCase());
                    QuestionCategory category = QuestionCategory.getFromName(q.category());
                    QuestionType type = q.type().equals("multiple") ? QuestionType.MULTIPLE_CHOICE : QuestionType.TRUE_FALSE;
                    QuestionEntity question = new QuestionEntity(
                            null,
                            q.question(),
                            difficulty,
                            type,
                            category,
                            q.correct_answer(),
                            q.incorrect_answers()
                    );
                    repository.save(question);
                    counter++;
                }
                try{
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            log.info(String.valueOf(counter) + " questions were added to the database");
        }
    }


    private List<ApiQuestion> getQuestionsFromApi(
            int questionsAmount,
            String token
    ) {
        String url = "https://opentdb.com/api.php";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("amount", 50)
                .queryParam("token", token);


        log.info("URL: " + builder.toUriString());
        QuestionApiResponse QuestionApiResponse = restTemplate.getForObject(builder.toUriString(), QuestionApiResponse.class);
        return QuestionApiResponse.results();
    }

    private String getAPISessionToken()  {
        String url = "https://opentdb.com/api_token.php?command=request";
        String response = restTemplate.getForObject(url, String.class);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(response);
            return root.path("token").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public QuestionResponse getQuestionFromDatabase(
            QuestionDifficulty difficulty,
            QuestionType type,
            QuestionCategory category
    ) {
        Optional<QuestionEntity> entity = repository.getQuestion(
                type == null ? null : type.name(),
                difficulty == null ? null : difficulty.name(),
                category == null ? null : category.name()
        );
        if (entity.isEmpty()) {
            throw new EntityNotFoundException("Question with given parameters not found");
        } else {
            QuestionResponse response = new QuestionResponse(
                    entity.get().getQuestion(),
                    entity.get().getDifficulty(),
                    entity.get().getType(),
                    entity.get().getCategory(),
                    entity.get().getCorrectAnswer(),
                    entity.get().getIncorrectAnswers()
            );
            return response;
        }
    }

}
