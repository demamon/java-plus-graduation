package ru.practicum.user.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.api.dto.user.AdminUserParam;
import ru.practicum.interaction.api.dto.user.NewUserRequest;
import ru.practicum.interaction.api.dto.user.UserDto;
import ru.practicum.interaction.api.dto.user.UserShortDto;
import ru.practicum.user.service.model.User;
import ru.practicum.user.service.service.UserService;


import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size) {
        AdminUserParam param = AdminUserParam.builder()
                .ids(ids)
                .from(from)
                .size(size)
                .build();
        return userService.getUsers(param);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequest user) {
        return userService.createUser(user);
    }

    @DeleteMapping("/{user-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable(name = "user-id") long userId) {
        userService.removeUser(userId);
    }

    @GetMapping("full/{user-id}")
    public User getUserFull(@PathVariable(name = "user-id") long userId) {
        return userService.getUserFull(userId);
    }

    @GetMapping("short/{user-id}")
    public UserShortDto getUserShort(@PathVariable(name = "user-id") long userId) {
        return userService.getUserShort(userId);
    }
}