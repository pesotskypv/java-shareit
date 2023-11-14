package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.exception.UserValidationException;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        checkFreeEmail(userDto);

        return userMapper.toUserDto(userRepository.createUser(userMapper.toUser(userDto)));
    }

    @Override
    public UserDto getUser(Long id) {
        return userMapper.toUserDto(userRepository.getUser(id)
                .orElseThrow(() -> new UserNotFoundException("Попытка получить несуществующего пользователя")));
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAllUsers().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        userRepository.getUser(id)
                .orElseThrow(() -> new UserNotFoundException("Попытка обновить несуществующего пользователя"));
        String name = userDto.getName();
        String email = userDto.getEmail();

        if (!Objects.equals(userDto.getId(), id)) {
            userDto.setId(id);
        }
        if (name != null && name.isBlank()) {
            String textError = "Имя пользователя не может быть пустым";

            log.debug("Валидация не пройдена: " + textError);
            throw new UserValidationException(textError);
        }
        if (email != null && email.isBlank()) {
            String textError = "Адрес электронной почты не может быть пустым";

            log.debug("Валидация не пройдена: " + textError);
            throw new UserValidationException(textError);
        }
        if (email != null) {
            checkFreeEmail(userDto);
        }

        return userMapper.toUserDto(userRepository.updateUser(userMapper.toUser(userDto)));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }

    private void checkFreeEmail(UserDto userDto) {
        if (userRepository.findAllUsers().stream()
                .filter(u -> !u.getId().equals(userDto.getId()))
                .anyMatch(u -> u.getEmail().equals(userDto.getEmail()))) {
            String textError = "Уже существует пользователь c электронной почтой: " + userDto.getEmail();

            log.debug("Валидация не пройдена: " + textError);
            throw new UserValidationException(textError);
        }
    }
}
