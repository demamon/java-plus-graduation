package ru.practicum.interaction.api.feign.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.interaction.api.dto.user.UserDto;
import ru.practicum.interaction.api.dto.user.UserShortDto;

@FeignClient(name = "user-service", path = "/admin/users")
public interface UserClient {
    @GetMapping("full/{userId}")
    public UserDto getUserFull(@PathVariable("userId") Long userId);

    @GetMapping("short/{userId}")
    public UserShortDto getUserShort(@PathVariable("userId") Long userId);
}
