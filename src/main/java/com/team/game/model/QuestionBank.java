package main.java.com.team.game.model;

import java.util.*;

public class QuestionBank {
    private List<Question> basicsQuestions;
    private List<Question> trigoQuestions;
    private List<Question> targetQuestions;

    public QuestionBank() {
        basicsQuestions = generateBasics();
        trigoQuestions = generateTrigo();
        targetQuestions = generateTarget();
    }

    // Questions for Basic Category
    private List<Question> generateBasics() {
        List<Question> q = new ArrayList<>();

        // Q1 - Formula
        q.add(new Question(
                "u = 20 m/s, t = 12s, a = 10 m/s². Which formula gives v?",
                "v = u + at",
                Arrays.asList("v = u + at", "s = ut + 1/2 at²", "v² = u² + 2as", "s = 1/2 (u+v)t")
        ));

        // Q2 - Numeric
        q.add(new Question(
                "If u = 0, a = 9.8 m/s², t = 5s. What is s?",
                "123"
        ));

        q.add(new Question(
                "Which formula represents displacement with initial velocity?",
                "s = ut + 1/2 at²",
                Arrays.asList("s = ut + 1/2 at²", "v = u + at", "F = ma", "E = mc²")
        ));

        // Q4 - Numeric
        q.add(new Question(
                "u = 15 m/s, v = 35 m/s, t = 4s. What is a?",
                "5",
                null

        ));

        // Q5 - Formula
        q.add(new Question(
                "Which of these equations is derived from Newton's second law?",
                "F = ma",
                Arrays.asList("F = ma", "v = u + at", "s = ut + 1/2 at²", "p = mv")
        ));

        // Q6 - Numeric
        q.add(new Question(
                "A car accelerates from rest at 2 m/s² for 6s. Find v.",
                "12",
                null

        ));

        // Q7 - Numeric
        q.add(new Question(
                "If s = 100m, u = 10 m/s, v = 30 m/s, find t using s = 1/2(u+v)t.",
                "5",
                null

        ));

        // Q8 - Formula
        q.add(new Question(
                "Which formula relates velocity squared to displacement?",
                "v² = u² + 2as",
                Arrays.asList("v² = u² + 2as", "s = ut + 1/2 at²", "p = mv", "v = u + at")

        ));

        // Q9 - Numeric
        q.add(new Question(
                "u = 25 m/s, a = -5 m/s². Find time to stop.",
                "5",
                null

        ));

        // Q10 - Numeric
        q.add(new Question(
                "A stone is dropped (u = 0). Time = 3s. Find displacement.",
                "44.1",
                null

        ));
        return q;
    }

    //Questions for Trigo Category
    private List<Question> generateTrigo() {
        List<Question> q = new ArrayList<>();
        q.add(new Question("Simplify: tan 45° + cos 60°",
                "1.5",
                Arrays.asList("1", "1.5", "√2/2", "2")
        ));
        q.add(new Question("Simplify: (1 - cos²θ) / sin²θ",
                "1",
                Arrays.asList("1", "cosθ", "tanθ", "cosec²θ")
        ));
        q.add(new Question("In a right triangle, θ = 30° and hypotenuse = 10. Find the length of the side opposite θ.",
                "5"
        ));
        q.add(new Question("If sinθ = 3/5, find cosθ (θ acute).",
                "0.8"));
        q.add(new Question("If tanθ = 4/3, find sinθ (θ acute).",
                "0.8"));
        q.add(new Question("Solve for θ: sinθ = 0.5, θ ∈ [0°, 60°].",
                "30"));
        q.add(new Question("Simplify: sin²θ + cos²θ.",
                "1"));
        q.add(new Question("If cosθ = 12/13, find tanθ (θ acute).",
                "0.417"));
        q.add(new Question("Find the exact value of sin45° × cos30° + cos45° × sin30°.",
                "0.966"));
        q.add(new Question("Solve for θ: tanθ = 1, θ ∈ [0°, 60°].",
                "45°"));

        return q;
    }

    //Questions for Target Category
    private List<Question> generateTarget() {
        List<Question> q = new ArrayList<>();
        q.add(new Question("A projectile is fired with u = 20 m/s at 30°. Find horizontal component of velocity.",
                "17.3"));
        q.add(new Question("A projectile is fired with u = 20 m/s at 30°. Find vertical component of velocity.",
                "10"));
        q.add(new Question("Time of flight formula is?",
                "T = 2u sinθ / g",
                Arrays.asList("T = 2u sinθ / g", "T = u² sin2θ / g", "T = u cosθ / g", "T = 2u cosθ / g")
        ));
        q.add(new Question("Range formula is?",
                "R = u² sin2θ / g",
                Arrays.asList("R = u² sin2θ / g", "R = 2u sinθ / g", "R = u cosθ / g", "R = u² / g")
        ));
        q.add(new Question("Max height formula is?",
                "H = u² sin²θ / 2g",
                Arrays.asList("H = u² sin²θ / 2g", "H = u² cos²θ / 2g", "H = u² / 2g", "H = u sinθ / g")
        ));
        q.add(new Question("u = 25 m/s, θ = 45°. Find time of flight (g = 9.8).",
                "3.61"));
        q.add(new Question("u = 25 m/s, θ = 45°. Find range (g = 9.8).",
                "63.7"));
        q.add(new Question("u = 25 m/s, θ = 30°. Find max height (g = 9.8).",
                "7.96"));
        q.add(new Question("A projectile lands back at same height. Relationship between launch and landing angles?",
                "Equal in magnitude, opposite in sign"));
        q.add(new Question("If time of flight = 4s, horizontal velocity = 15 m/s. Find range.",
                "60.0"));

        return q;
    }

    // Getter mthods to help obtain question
    public List<Question> getBasics() {
        return basicsQuestions;
    }

    public List<Question> getTrigo() {
        return trigoQuestions;
    }

    public List<Question> getTarget() {
        return targetQuestions;
    }
}
