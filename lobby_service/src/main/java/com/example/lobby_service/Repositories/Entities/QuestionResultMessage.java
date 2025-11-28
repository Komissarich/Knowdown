package com.example.lobby_service.Repositories.Entities;

import java.util.Map;

public record QuestionResultMessage(
        String type,           // "QUESTION_RESULTS"
        Map<String, Integer> points  // имя → очки (0 если не угадал или не успел)
) {}