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
import knowdown.question_service.dto.QuestionsRequest;
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

        final int needed = AppConfig.isDebug ? 300 : 4000;
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

    public List<QuestionResponse> getQuestionsFromDatabase(
            QuestionsRequest request
    ) {
        List<String> questionTypes = new ArrayList<String>();
        request.types().forEach(t -> questionTypes.add(t.name()));
        List<String> questionDifficulties = new ArrayList<String>();
        request.difficulties().forEach(d -> questionDifficulties.add(d.name()));
        List<String> questionCategories = new ArrayList<String>();
        request.categories().forEach(c -> questionCategories.add(c.name()));

        List<QuestionEntity> questions = repository.getQuestions(
                request.amount(),
                questionTypes,
                questionDifficulties,
                questionCategories
        );

        if (questions.isEmpty()) {
            throw new EntityNotFoundException("Question with given parameters not found");
        } else {
            List<QuestionResponse> result = new ArrayList<QuestionResponse>();
            questions.forEach(q -> {
                String question = fixQuestionString(q.getQuestion());
                String correct = fixQuestionString(q.getCorrectAnswer());
                List<String> incorrect = new ArrayList<String>();
                q.getIncorrectAnswers().forEach(a -> {
                    incorrect.add(fixQuestionString(a));
                });

                result.add(
                        new QuestionResponse(
                                question,
                                q.getDifficulty(),
                                q.getType(),
                                q.getCategory(),
                                correct,
                                incorrect
                        )
                );
            });
            return result;
        }

    }

    String fixQuestionString(String str) {
        String result = str.replace("&#039", "");
        result = result.replace("&quot;", "'");
        return result;
    }
}
