package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.utils.ResourcePool.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @Test
    void createUser() throws Exception {
        UserDto userDto = read(createUserRequest, UserDto.class);
        UserDto savedUserDto = read(createdUserDto, UserDto.class);

        Mockito.when(userService.createUser(userDto)).thenReturn(savedUserDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedUserDto)));

        Mockito.verify(userService, Mockito.times(1)).createUser(userDto);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void getUser() throws Exception {
        Long userId = 1L;
        UserDto userDto = read(createdUserDto, UserDto.class);

        Mockito.when(userService.getUser(userId)).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + userId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userDto)));

        Mockito.verify(userService, Mockito.times(1)).getUser(userId);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void findAllUsers() throws Exception {
        List<UserDto> usersDto = read(usersDtoList, new TypeReference<ArrayList<UserDto>>() {});

        Mockito.when(userService.findAllUsers()).thenReturn(usersDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(usersDto)));

        Mockito.verify(userService, Mockito.times(1)).findAllUsers();
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void updateUser() throws Exception {
        Long userId = 1L;
        UserDto userDto = read(updateNameUserRequest, UserDto.class);
        UserDto updatedUserDto = read(updatedNameUsersDto, UserDto.class);

        Mockito.when(userService.updateUser(userId, userDto)).thenReturn(updatedUserDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/" + userId)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(updatedUserDto)));

        Mockito.verify(userService, Mockito.times(1)).updateUser(userId, userDto);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void deleteUser() throws Exception {
        Long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + userId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService, Mockito.times(1)).deleteUser(userId);
        Mockito.verifyNoMoreInteractions(userService);
    }
}