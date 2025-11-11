package knowdown.question_service.database;

import jakarta.persistence.*;
import knowdown.question_service.QuestionCategory;
import knowdown.question_service.QuestionDifficulty;
import knowdown.question_service.QuestionType;
import org.hibernate.annotations.Collate;
import org.hibernate.annotations.Type;

import java.util.List;

@Table(name = "questions")
@Entity
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "question")
    private String question;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    private QuestionDifficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private QuestionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private QuestionCategory category;

    @Column(name = "correct_answer")
    private String correctAnswer;

    @Column(name = "incorrect_answers")
    private List<String> incorrectAnswers;

    public QuestionEntity() {
    }

    public QuestionEntity(
            Long id,
            String question,
            QuestionDifficulty difficulty,
            QuestionType type,
            QuestionCategory category,
            String correctAnswer,
            List<String> incorrectAnswers
    ) {
        this.id = id;
        this.question = question;
        this.difficulty = difficulty;
        this.type = type;
        this.category = category;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public QuestionDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(QuestionDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public QuestionCategory getCategory() {
        return category;
    }

    public void setCategory(QuestionCategory category) {
        this.category = category;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(List<String> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }
}
