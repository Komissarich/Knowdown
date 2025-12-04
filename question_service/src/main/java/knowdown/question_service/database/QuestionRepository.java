package knowdown.question_service.database;

import knowdown.question_service.QuestionCategory;
import knowdown.question_service.QuestionDifficulty;
import knowdown.question_service.QuestionType;
import knowdown.question_service.dto.QuestionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
//
//    @Query(value = """
//        SELECT *
//        FROM questions
//        WHERE (:questionType is NULL OR type = :questionType)
//        AND (:questionDifficulty is NULL OR difficulty = :questionDifficulty)
//        AND (:questionCategory is NULL OR category = :questionCategory)
//        ORDER BY RANDOM()
//        LIMIT 1
//    """, nativeQuery = true)
//    Optional<QuestionEntity> getQuestion(
//            @Param("questionType") String questionType,
//            @Param("questionDifficulty") String questionDifficulty,
//            @Param("questionCategory") String questionCategory
//    );

    @Query(value = """
        SELECT *
        FROM questions
        WHERE (type = ANY(:questionTypes))
        AND (difficulty = ANY(:questionDifficulties))
        AND (category = ANY(:questionCategories))
        ORDER BY RANDOM()
        LIMIT :amount
    """, nativeQuery = true)
    List<QuestionEntity> getQuestions(
            @Param("amount") Integer amount,
            @Param("questionTypes") List<String> questionTypes,
            @Param("questionDifficulties") List<String> questionDifficulties,
            @Param("questionCategories") List<String> questionCategories
    );
}
