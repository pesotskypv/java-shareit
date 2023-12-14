package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDto toUserDto(User user);

    User toUser(UserDto userDto);
}