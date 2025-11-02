package ru.practicum.analyzer.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.mapper.UserActionMapper;
import ru.practicum.analyzer.model.UserAction;
import ru.practicum.analyzer.repository.UserActionRepository;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserActionHandlerImpl implements UserActionHandler {

    private final UserActionRepository userActionRepository;

    @Transactional
    @Override
    public void handle(UserActionAvro action) {
        Long eventId = action.getEventId();
        Long userId = action.getUserId();

        log.debug("Обработка действия пользователя: userId={}, eventId={}, actionType={}",
                userId, eventId, action.getActionType());

        UserAction existingAction = userActionRepository.findByEventIdAndUserId(eventId, userId);

        if (existingAction == null) {
            UserAction newUserAction = UserActionMapper.mapToUserAction(action);
            userActionRepository.save(newUserAction);
            log.info("Создано новое действие пользователя: userId={}, eventId={}, mark={}",
                    userId, eventId, newUserAction.getMark());
        } else {
            updateExistingActionIfNeeded(existingAction, action);
        }
    }

    private void updateExistingActionIfNeeded(UserAction existingAction, UserActionAvro action) {
        Float newMark = UserActionMapper.mapToType(action.getActionType());
        Float currentMark = existingAction.getMark();

        if (currentMark == null || newMark > currentMark) {
            existingAction.setMark(newMark);
            existingAction.setTimestamp(action.getTimestamp());
            log.info("Обновлено действие пользователя: userId={}, eventId={}, старыйMark={}, новыйMark={}",
                    existingAction.getUserId(), existingAction.getEventId(), currentMark, newMark);
        } else {
            log.debug("Действие не требует обновления: userId={}, eventId={}, текущийMark={}, новыйMark={}",
                    existingAction.getUserId(), existingAction.getEventId(), currentMark, newMark);
        }
    }
}
