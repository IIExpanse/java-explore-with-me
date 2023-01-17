package ru.yandex.practicum.ewm.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.ewm.user.dto.UserDto;
import ru.yandex.practicum.ewm.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "events", ignore = true)
    @Mapping(target = "requests", ignore = true)
    User mapToModel(UserDto userDto);

    UserDto mapToDto(User user);
}
