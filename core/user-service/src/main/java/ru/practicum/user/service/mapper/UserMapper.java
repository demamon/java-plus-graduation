package ru.practicum.user.service.mapper;

import ru.practicum.interaction.api.dto.user.NewUserRequest;
import ru.practicum.interaction.api.dto.user.UserDto;
import ru.practicum.interaction.api.dto.user.UserShortDto;
import ru.practicum.user.service.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static List<UserDto> mapToUserDto(Iterable<User> users) {
        List<UserDto> usersResult = new ArrayList<>();

        for (User user : users) {
            usersResult.add(mapToUserDto(user));
        }

        return usersResult;
    }

    public static UserShortDto mapToUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static User mapFromRequest(NewUserRequest user) {
        return new User(
                user.getEmail(),
                user.getName()
        );
    }
}
