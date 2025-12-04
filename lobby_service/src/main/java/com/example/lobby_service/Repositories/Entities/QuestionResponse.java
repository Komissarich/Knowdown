package com.example.lobby_service.Repositories.Entities;

import java.util.List;

public class QuestionResponse {
    private List<Question> quiz_questions;

    public List<Question> getQuiz_questions() {
        return quiz_questions;
    }

    public void setQuiz_questions(List<Question> quiz_questions) {
        this.quiz_questions = quiz_questions;
    }
}
