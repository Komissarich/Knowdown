package com.example.lobby_service.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
  // ← эта аннотация сама создаст конструктор
public class CountDownService {
    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    @Autowired
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public CountDownService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;

    }

    public void startCountdown(String lobbyName) {
        // Отменяем, если уже был
        cancelIfRunning(lobbyName);

        AtomicInteger count = new AtomicInteger(7);

        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> {
            int value = count.decrementAndGet();

            if (value > 0) {
                messagingTemplate.convertAndSend("/topic/lobby/" + lobbyName + "/countdown",
                        new CountDownMessage("COUNTDOWN", value));
            } else if (value == 0) {
                messagingTemplate.convertAndSend("/topic/lobby/" + lobbyName + "/countdown",
                        new CountDownMessage("START", null));
                cancelIfRunning(lobbyName); // останавливаем
            }
        }, 0, 1, TimeUnit.SECONDS);

        tasks.put(lobbyName, task);
    }

    private void cancelIfRunning(String lobbyName) {
        ScheduledFuture<?> old = tasks.remove(lobbyName);
        if (old != null) old.cancel(false);
    }
}

