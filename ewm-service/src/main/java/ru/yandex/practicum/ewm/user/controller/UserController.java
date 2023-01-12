package ru.yandex.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.user.dto.UserDto;
import ru.yandex.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody @Valid UserDto userDto) {
        userDto.setId(null);
        return ResponseEntity.ok(service.addUser(userDto));
    }

    @GetMapping
    public ResponseEntity<Collection<UserDto>> getUsers(
            @RequestParam(required = false) Collection<Long> ids,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(service.getUsers(ids, from, size));
    }

    @DeleteMapping(path = "/{userId}")
    @ResponseStatus(code = HttpStatus.OK)
    public void deleteUser(@PathVariable Long userId) {
        service.deleteUser(userId);
    }
}
