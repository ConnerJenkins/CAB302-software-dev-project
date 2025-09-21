package test.java.com.team.game.controller;
import main.java.com.team.game.model.Question;
import main.java.com.team.game.model.QuestionBank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class QuestionBankTest {
    private QuestionBank questionBank;

    @BeforeEach
    void setUp() {
        questionBank = new QuestionBank();
    }

    @Test
    void basicsQuestionsShouldNotBeEmpty() {
        List<Question> basics = questionBank.getBasics();
        assertNotNull(basics, "Basics questions list should not be null");
        assertFalse(basics.isEmpty(), "Basics questions list should not be empty");
    }

    @Test
    void trigoQuestionsShouldNotBeEmpty() {
        List<Question> trigo = questionBank.getTrigo();
        assertNotNull(trigo, "Trigo questions list should not be null");
        assertFalse(trigo.isEmpty(), "Trigo questions list should not be empty");
    }

    @Test
    void targetQuestionsShouldNotBeEmpty() {
        List<Question> target = questionBank.getTarget();
        assertNotNull(target, "Target questions list should not be null");
        assertFalse(target.isEmpty(), "Target questions list should not be empty");
    }

    @Test
    void eachBasicQuestionShouldHaveValidTextAndAnswer() {
        for (Question q : questionBank.getBasics()) {
            assertNotNull(q.getText(), "Question text should not be null");
            assertFalse(q.getText().isBlank(), "Question text should not be blank");

            assertNotNull(q.getAnswer(), "Answer should not be null");
            assertFalse(q.getAnswer().isBlank(), "Answer should not be blank");
        }
    }
}
