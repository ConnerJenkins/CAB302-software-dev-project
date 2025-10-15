package test.java.com.team.game.controller;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import main.java.com.team.game.controller.TargetGameController;
import main.java.com.team.game.model.GameMode;
import main.java.com.team.game.model.GameSession;
import main.java.com.team.game.model.User;
import main.java.com.team.game.service.GameService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TargetGameController}.
 * Uses lightweight JavaFX controls injected via reflection to validate input handling and state.
 */
public class TargetGameControllerTest {

    private TargetGameController controller;

    // Fake UI controls injected reflectively into the controller
    private Label scoreLabel;
    private Label strikesLabel;
    private Label statusLabel;
    private Label questionLabel;
    private TextField answerField;
    private Button fireBtn;
    private Button nextBtn;
    private Button newGameBtn;
    private Button returnBtn;
    private Canvas canvas;

    private GameService mockService;
    private GameSession mockSession;
    private User mockUser;

    /**
     * Ensures JavaFX platform is started once for tests that touch controls.
     * Ignores the error if already initialized.
     */
    @BeforeAll
    static void initJavaFx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }
    }

    /**
     * Creates a fresh controller, injects stubbed controls and state,
     * and wires a mocked {@link GameService} before each test.
     */
    @BeforeEach
    void setUp() throws Exception {
        controller = new TargetGameController();

        scoreLabel = new Label();
        strikesLabel = new Label();
        statusLabel = new Label();
        questionLabel = new Label();
        answerField = new TextField();
        fireBtn = new Button();
        nextBtn = new Button();
        newGameBtn = new Button();
        returnBtn = new Button();
        canvas = new Canvas(800, 420);

        fireBtn.setDisable(false);
        nextBtn.setDisable(true);
        newGameBtn.setDisable(true);
        returnBtn.setDisable(true);

        setPrivate(controller, "scoreLabel", scoreLabel);
        setPrivate(controller, "strikesLabel", strikesLabel);
        setPrivate(controller, "statusLabel", statusLabel);
        setPrivate(controller, "questionLabel", questionLabel);
        setPrivate(controller, "answerField", answerField);
        setPrivate(controller, "fireBtn", fireBtn);
        setPrivate(controller, "nextBtn", nextBtn);
        setPrivate(controller, "newGameBtn", newGameBtn);
        setPrivate(controller, "returnBtn", returnBtn);
        setPrivate(controller, "canvas", canvas);

        setPrivate(controller, "roundActive", true);
        setPrivate(controller, "animating", false);

        mockService = mock(GameService.class);
        mockSession = mock(GameSession.class);
        mockUser = new User(1, "tester", Instant.now());
        when(mockService.startRound(any(User.class), eq(GameMode.TARGET))).thenReturn(mockSession);

        setPrivate(controller, "gameService", mockService);
        setPrivate(controller, "currentUser", mockUser);

        scoreLabel.setText("Score: 0");
        strikesLabel.setText("Strikes: 0 / 3");
        statusLabel.setText("Ready…");
    }

    // TESTS

    /** When no speed is provided, the controller should prompt for input. */
    @Test
    void fire_withEmptyInput_showsPrompt() throws Exception {
        answerField.setText("");
        invokePrivate(controller, "handleFire");
        assertEquals("Enter a launch speed first.", statusLabel.getText());
    }

    /** Non-numeric speed should trigger a validation message. */
    @Test
    void fire_withNonNumeric_showsValidation() throws Exception {
        answerField.setText("abc");
        invokePrivate(controller, "handleFire");
        assertEquals("Please enter a valid number (m/s).", statusLabel.getText());
    }

    /** Negative speed should be rejected with a clear error. */
    @Test
    void fire_withNegative_showsValidation() throws Exception {
        answerField.setText("-3.2");
        invokePrivate(controller, "handleFire");
        assertEquals("Speed must be > 0.", statusLabel.getText());
    }

    // Reflection helpers

    /**
     * Sets a private field on the target object.
     *
     * @param target    instance containing the field
     * @param fieldName private field name
     * @param value     value to assign
     */
    private static void setPrivate(Object target, String fieldName, Object value) throws Exception {
        Field f = getField(target.getClass(), fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    /**
     * Reads a private field from the target object.
     *
     * @param target    instance containing the field
     * @param fieldName private field name
     * @return field value
     */
    private static Object getPrivate(Object target, String fieldName) throws Exception {
        Field f = getField(target.getClass(), fieldName);
        f.setAccessible(true);
        return f.get(target);
    }

    /**
     * Invokes a private zero-arg method on the target object.
     *
     * @param target     instance containing the method
     * @param methodName private method name
     */
    private static void invokePrivate(Object target, String methodName, Class<?>... paramTypes) throws Exception {
        Method m = getMethod(target.getClass(), methodName, paramTypes);
        m.setAccessible(true);
        m.invoke(target);
    }

    /**
     * Traverses the class hierarchy to find a declared field.
     *
     * @param cls  starting class
     * @param name field name
     * @return the {@link Field} if found
     * @throws NoSuchFieldException if not found
     */
    private static Field getField(Class<?> cls, String name) throws NoSuchFieldException {
        Class<?> c = cls;
        while (c != null) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignore) {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    /**
     * Traverses the class hierarchy to find a declared method.
     *
     * @param cls        starting class
     * @param name       method name
     * @param paramTypes parameter types
     * @return the {@link Method} if found
     * @throws NoSuchMethodException if not found
     */
    private static Method getMethod(Class<?> cls, String name, Class<?>... paramTypes) throws NoSuchMethodException {
        Class<?> c = cls;
        while (c != null) {
            try {
                return c.getDeclaredMethod(name, paramTypes);
            } catch (NoSuchMethodException ignore) {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchMethodException(name);
    }
}
