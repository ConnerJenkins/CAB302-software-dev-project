package com.team.game.model;

import java.util.List;

public class Question {
    private String text;
    private String answer;
    private List<String> options;  // optional (for MCQ in Basics)

    public Question(String text, String answer) {
        this.text = text;
        this.answer = answer;
        this.options = null;
    }

    public Question(String text, String answer, List<String> options) {
        this.text = text;
        this.answer = answer;
        this.options = options;
    }

    public String getText() {
        return text;
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getOptions() {
        return options;
    }

    @Override
    public String toString() {
        return text;
    }
}
