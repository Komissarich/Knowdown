package com.example.lobby_service.Repositories.Entities;

import java.util.List;

public class Question {
    private String type;
    private String difficulty;
    private String category;
    private String question;
    private String correctAnswer;
    private List<String> incorrectAnswers;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
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

    @Override
    public String toString() {
        return "Question{" +
                "type='" + type + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", category='" + category + '\'' +
                ", question='" + question + '\'' +
                ", correct_answer='" + correctAnswer + '\'' +
                ", incorrect_answers=" + incorrectAnswers +
                '}';
    }
}
