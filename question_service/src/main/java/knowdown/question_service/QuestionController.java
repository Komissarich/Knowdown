package knowdown.question_service;

import knowdown.question_service.dto.QuestionResponse;
import knowdown.question_service.dto.QuestionsRequest;
import knowdown.question_service.dto.QuizResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
    private final QuestionService questionService;

    public QuestionController (QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/give_questions")
    public ResponseEntity<QuizResponse> getQuestion(
            @RequestBody QuestionsRequest request
    ) {
        if (
                request.categories().isEmpty() ||
                request.types().isEmpty() ||
                request.difficulties().isEmpty() ||
                request.amount() == 0
        ){
            return ResponseEntity.internalServerError().build();
        }

        try {
            return ResponseEntity.ok(new QuizResponse(questionService.getQuestionsFromDatabase(request))
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
