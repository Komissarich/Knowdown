package knowdown.question_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/question")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
    private final QuestionService questionService;

    public QuestionController (QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ResponseEntity<Question> getQuestion(
            @RequestParam(name = "difficulty", required = false) QuestionDifficulty difficulty,
            @RequestParam(name = "type", required = false) QuestionType type
    ) {
        log.info("Called getQuestion with parameters: difficulty=" + difficulty + "; type="+type);


        try {
            return ResponseEntity.ok(questionService.getQuestionFromApi(type, difficulty));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
