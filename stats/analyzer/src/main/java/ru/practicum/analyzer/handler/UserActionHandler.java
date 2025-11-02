package ru.practicum.analyzer.handler;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface UserActionHandler {

    void handle(UserActionAvro userActionAvro);
}
