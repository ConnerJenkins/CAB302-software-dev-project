package test.java.com.team.game.controller;

import main.java.com.team.game.model.Question;
import main.java.com.team.game.model.QuestionBank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link QuestionBank}.
 * Ensures all question sets are valid, complete, and distinct.
 */
public class QuestionBankTest {

    /** Shared QuestionBank instance created before each test. */
    private QuestionBank questionBank;

    /** Initializes a new {@link QuestionBank} to ensure clean state. */
    @BeforeEach
    void setUp() {
        questionBank = new QuestionBank();
    }

    /** Verifies that the Basics question list is initialized and non-empty. */
    @Test
    void basicsQuestionsShouldNotBeEmpty() {
        List<Question> basics = questionBank.getBasics();
        assertNotNull(basics, "Basics questions list should not be null");
        assertFalse(basics.isEmpty(), "Basics questions list should not be empty");
    }

    /** Verifies that the Trigonometry question list is initialized and non-empty. */
    @Test
    void trigoQuestionsShouldNotBeEmpty() {
        List<Question> trigo = questionBank.getTrigo();
        assertNotNull(trigo, "Trigo questions list should not be null");
        assertFalse(trigo.isEmpty(), "Trigo questions list should not be empty");
    }

    /** Checks that the Basics question set contains the expected number of questions (10). */
    @Test
    void basicsShouldContainExpectedNumberOfQuestions() {
        List<Question> basics = questionBank.getBasics();
        assertEquals(10, basics.size(),
                "Expected 10 basic questions but found only " + basics.size());
    }

    /** Ensures all questions in both banks have non-null, non-empty text. */
    @Test
    void allQuestionsShouldHaveValidText() {
        List<Question> basics = questionBank.getBasics();
        List<Question> trigo = questionBank.getTrigo();

        for (Question q : basics) {
            assertNotNull(q.getText(), "Question text should not be null");
            assertFalse(q.getText().trim().isEmpty(), "Question text should not be empty");
        }

        for (Question q : trigo) {
            assertNotNull(q.getText(), "Question text should not be null");
            assertFalse(q.getText().trim().isEmpty(), "Question text should not be empty");
        }
    }

    /** Ensures all questions in both banks have valid (non-empty) answers. */
    @Test
    void questionsShouldHaveAtLeastOneAnswer() {
        List<Question> basics = questionBank.getBasics();
        List<Question> trigo = questionBank.getTrigo();

        for (Question q : basics) {
            assertNotNull(q.getAnswer(), "Answers list should not be null");
            assertFalse(q.getAnswer().isEmpty(), "Answers list should not be empty");
        }

        for (Question q : trigo) {
            assertNotNull(q.getAnswer(), "Answers list should not be null");
            assertFalse(q.getAnswer().isEmpty(), "Answers list should not be empty");
        }
    }

    /** Verifies there are no duplicate questions within each bank. */
    @Test
    void questionsShouldBeUniqueInEachBank() {
        List<Question> basics = questionBank.getBasics();
        List<Question> trigo = questionBank.getTrigo();

        Set<String> basicQuestionsText = new HashSet<>();
        for (Question q : basics) {
            assertTrue(basicQuestionsText.add(q.getText()), "Duplicate question found in basics");
        }

        Set<String> trigoQuestionsText = new HashSet<>();
        for (Question q : trigo) {
            assertTrue(trigoQuestionsText.add(q.getText()), "Duplicate question found in trigo");
        }
    }

    /** Ensures there is no overlap between the Basics and Trigonometry question sets. */
    @Test
    void basicsAndTrigoShouldNotOverlap() {
        List<Question> basics = questionBank.getBasics();
        List<Question> trigo = questionBank.getTrigo();

        Set<String> basicsText = new HashSet<>();
        for (Question q : basics) {
            basicsText.add(q.getText());
        }

        for (Question q : trigo) {
            assertFalse(basicsText.contains(q.getText()),
                    "Question appears in both basics and trigo: " + q.getText());
        }
    }
}
