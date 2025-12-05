package com.example.lobby_service.Repositories.Entities;

import java.util.List;

public class ApiGetQuestions {
    private int amount;
    private List<String> difficulties;
    private List<String> types;
    private List<String> categories;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public List<String> getDifficulties() {
        return difficulties;
    }

    public void setDifficulties(List<String> difficulties) {
        this.difficulties = difficulties;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
