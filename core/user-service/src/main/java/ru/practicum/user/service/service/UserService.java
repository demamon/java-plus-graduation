package ru.practicum.user.service.service;

import ru.practicum.interaction.api.dto.user.AdminUserParam;
import ru.practicum.interaction.api.dto.user.NewUserRequest;
import ru.practicum.interaction.api.dto.user.UserDto;
import ru.practicum.interaction.api.dto.user.UserShortDto;
import ru.practicum.user.service.model.User;


import java.util.List;

public interface UserService {
    List<UserDto> getUsers(AdminUserParam param);

    UserDto createUser(NewUserRequest user);

    void removeUser(long userId);

    User getUserFull(long userId);

    UserShortDto getUserShort(long userId);
}
