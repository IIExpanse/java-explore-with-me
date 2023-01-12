package ru.yandex.practicum.ewm.user.service;

import ru.yandex.practicum.ewm.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto addUser(UserDto userDto);

    Collection<UserDto> getUsers(Collection<Long> ids, int from, int size);

    void deleteUser(long id);
}
