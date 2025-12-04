package com.example.lobby_service.Repositories.Entities;

import java.util.Map;

public record QuestionResultMessage(
        String type,
        Map<String, Integer> points
) {}