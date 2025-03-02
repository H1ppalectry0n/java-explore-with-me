package ru.yandex.practicum.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.EwmStatsClient;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserAdminController {

    private final UserAdminService userAdminService;
    private final EwmStatsClient ewmStatsClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createNewUser(@RequestBody @Valid UserDto userDto, HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("UserAdmin: create new user {}", userDto.toString());
        return userAdminService.createNewUser(userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(name = "userId") long userId, HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("UserAdmin: deleting user with id = {}", userId);
        userAdminService.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(name = "ids", required = false) List<Long> ids,
                                  @RequestParam(name = "from", required = false, defaultValue = "0") long from,
                                  @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                  HttpServletRequest request) {
        ewmStatsClient.hit(request);
        log.info("UserAdmin: getting users with ids = {}, from = {}, size = {}", ids, from, size);
        return userAdminService.getUsers(from, size, ids);
    }
}
