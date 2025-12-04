package com.example.lobby_service.Repositories.Entities;

import java.util.List;

public class QuestionRequest {
    private int question_count;
    List<String> categories;

    public int getQuestion_count() {
        return question_count;
    }

    public void setQuestion_count(int question_count) {
        this.question_count = question_count;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "QuestionRequest{" +
                "number_of_rounds=" + question_count +
                ", categories=" + categories +
                '}';
    }
}
