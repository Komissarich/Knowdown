package knowdown.question_service;

import knowdown.question_service.dto.QuestionRequest;
import knowdown.question_service.dto.QuestionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/question")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
    private final QuestionService questionService;

    public QuestionController (QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> getQuestion(
            @RequestBody QuestionRequest request
    ) {
        log.info("Called getQuestion with parameters: difficulty = " + request.difficulty() + "; type = "+request.type() + "; category = " + request.category());
        try {
            return ResponseEntity.ok(questionService.getQuestionFromApi(request.type(), request.difficulty(), request.category()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
