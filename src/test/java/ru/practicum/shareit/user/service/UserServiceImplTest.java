package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.exception.VoidMethodException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = {UserServiceImpl.class, UserMapperImpl.class})
public class UserServiceImplTest {

    @MockBean
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @SpyBean
    private UserMapper userMapper;

    @Test
    void createUser_returnsUserDtoWhenCreated() {
        UserDto inUserDto = UserDto.builder().name("user").email("user@user.com").build();
        User inUser = User.builder().name(inUserDto.getName()).email(inUserDto.getEmail()).build();
        User outUser = User.builder().id(1L).name(inUserDto.getName()).email(inUserDto.getEmail()).build();
        UserDto expectedUserDto = UserDto.builder().id(outUser.getId()).name(outUser.getName())
                .email(outUser.getEmail()).build();

        Mockito.when(userRepository.save(inUser)).thenReturn(outUser);

        UserDto actualResponseUserDto = userService.createUser(inUserDto);

        Assertions.assertEquals(expectedUserDto, actualResponseUserDto);
        Mockito.verify(userRepository, Mockito.times(1)).save(inUser);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(userMapper, Mockito.times(1)).toUser(inUserDto);
        Mockito.verify(userMapper, Mockito.times(1)).toUserDto(outUser);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUser_returnsUserDtoWhenFound() {
        Long userId = 1L;
        User originalUser = User.builder().id(userId).name("user").email("user@user.com").build();
        UserDto expectedUserDto = UserDto.builder().id(originalUser.getId()).name(originalUser.getName())
                .email(originalUser.getEmail()).build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(originalUser));

        UserDto actualResponseUserDto = userService.getUser(userId);

        Assertions.assertEquals(expectedUserDto, actualResponseUserDto);
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(userMapper, Mockito.times(1)).toUserDto(originalUser);
        Mockito.verifyNoMoreInteractions(userMapper);
    }

    @Test
    void findAllUsers_returnsUsersDtoWhenFound() {
        User originalUser = User.builder().id(1L).name("user").email("user@user.com").build();
        List<User> outUsers = Collections.singletonList(originalUser);
        List<UserDto> expectedUserDto = Collections.singletonList(UserDto.builder().id(originalUser.getId())
                .name(originalUser.getName()).email(originalUser.getEmail()).build());

        Mockito.when(userRepository.findAll()).thenReturn(outUsers);

        List<UserDto> actualResponseUsersDto = userService.findAllUsers();

        Assertions.assertEquals(expectedUserDto, actualResponseUsersDto);
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(userMapper, Mockito.times(1)).toUserDto(originalUser);
        Mockito.verifyNoMoreInteractions(userMapper);
    }

    @Test
    void updateUser_returnsUserDtoWhenUpdated() {
        Long userId = 1L;
        UserDto inUserDto = UserDto.builder().id(userId).name("userUpd").email("userUpd@user.com").build();
        User originalUser = User.builder().id(userId).name("user").email("user@user.com").build();
        User inUser = User.builder().id(userId).name(inUserDto.getName()).email(inUserDto.getEmail()).build();
        UserDto expectedUserDto = UserDto.builder().id(inUser.getId()).name(inUser.getName()).email(inUser.getEmail())
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(originalUser));
        Mockito.when(userRepository.save(inUser)).thenReturn(inUser);

        UserDto actualResponseUserDto = userService.updateUser(userId, inUserDto);

        Assertions.assertEquals(expectedUserDto, actualResponseUserDto);
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(userRepository, Mockito.times(1)).save(inUser);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(userMapper, Mockito.times(1)).toUserDto(inUser);
        Mockito.verifyNoMoreInteractions(userMapper);
    }

    @Test
    void updateUser_NameIsBlankReturnsEntityValidationException() {
        Long userId = 1L;
        UserDto inUserDto = UserDto.builder().id(userId).name("").email("userUpd@user.com").build();
        User originalUser = User.builder().id(userId).name("user").email("user@user.com").build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(originalUser));

        Exception exception = assertThrows(EntityValidationException.class, () ->
                userService.updateUser(userId, inUserDto));

        assertTrue(exception.getMessage().contains("Имя пользователя не может быть пустым"));
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_EmailIsBlankReturnsEntityValidationException() {
        Long userId = 1L;
        UserDto inUserDto = UserDto.builder().id(userId).name("userUpd").email("").build();
        User originalUser = User.builder().id(userId).name("user").email("user@user.com").build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(originalUser));

        Exception exception = assertThrows(EntityValidationException.class, () ->
                userService.updateUser(userId, inUserDto));

        assertTrue(exception.getMessage().contains("Адрес электронной почты не может быть пустым"));
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUser() {
        Long userId = 1L;

        Mockito.doThrow(VoidMethodException.class).when(userRepository).deleteById(userId);

        Exception exception = assertThrows(VoidMethodException.class, () ->
                userService.deleteUser(userId));

        assertEquals(exception.getClass(), VoidMethodException.class);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }
}
