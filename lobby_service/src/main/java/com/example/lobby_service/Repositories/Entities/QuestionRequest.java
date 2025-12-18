package com.example.lobby_service.Repositories.Entities;

import java.util.List;

public class QuestionRequest {
    private int amount;
    List<String> categories;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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
                "number_of_rounds=" + amount +
                ", categories=" + categories +
                '}';
    }
}
