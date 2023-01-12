package ru.yandex.practicum.ewm.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.user.exception.DuplicateEmailException;
import ru.yandex.practicum.ewm.user.exception.UserNotFoundException;
import ru.yandex.practicum.ewm.user.dto.UserDto;
import ru.yandex.practicum.ewm.user.mapper.UserMapper;
import ru.yandex.practicum.ewm.user.model.User;
import ru.yandex.practicum.ewm.user.repository.UserRepository;
import ru.yandex.practicum.ewm.user.service.UserService;

import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        if (repository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateEmailException("Пользователь с такой электронной почтой уже существует.");
        }
        User user = repository.save(mapper.mapToModel(userDto));

        log.debug("Добавлен новый пользователь: {}", user);
        return mapper.mapToDto(user);
    }

    @Override
    public Collection<UserDto> getUsers(Collection<Long> ids, int from, int size) {
        if (ids != null && !ids.isEmpty()) {
            log.trace("Запрошен список пользователей с id: {}", ids);
            return repository.findAllByIdIn(ids, Pageable.ofSize(size)).stream()
                    .skip(from)
                    .map(mapper::mapToDto)
                    .collect(Collectors.toList());

        } else {
            log.trace("Запрошен список всех пользователей");
            return repository.findAll(Pageable.ofSize(size)).stream()
                    .skip(from)
                    .map(mapper::mapToDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);

        } else throw new UserNotFoundException(
                String.format("Ошибка при удалении пользователя: пользователь с id=%d не найден", id));
    }
}
