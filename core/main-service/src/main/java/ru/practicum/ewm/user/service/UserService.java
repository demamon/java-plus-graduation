package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.AdminUserParam;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(AdminUserParam param);

    UserDto createUser(NewUserRequest user);

    void removeUser(long userId);
}
