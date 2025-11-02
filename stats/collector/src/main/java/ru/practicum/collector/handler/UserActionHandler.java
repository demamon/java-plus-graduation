package ru.practicum.collector.handler;

import ru.practicum.ewm.grpc.stats.event.UserActionProto;

public interface UserActionHandler {
    void handle(UserActionProto userActionProto);
}
