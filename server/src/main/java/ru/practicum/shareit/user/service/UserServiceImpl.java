package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));
    }

    @Override
    public UserDto getUser(Long id) {
        return userMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Попытка получить несуществующего пользователя")));
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Попытка обновить несуществующего пользователя"));
        String name = userDto.getName();
        String email = userDto.getEmail();

        if (name != null && name.isBlank()) {
            String textError = "Имя пользователя не может быть пустым";

            log.debug("Валидация не пройдена: " + textError);
            throw new EntityValidationException(textError);
        }
        if (email != null && email.isBlank()) {
            String textError = "Адрес электронной почты не может быть пустым";

            log.debug("Валидация не пройдена: " + textError);
            throw new EntityValidationException(textError);
        }
        if (email != null) {
            user.setEmail(email);
        }
        if (name != null) {
            user.setName(name);
        }

        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
