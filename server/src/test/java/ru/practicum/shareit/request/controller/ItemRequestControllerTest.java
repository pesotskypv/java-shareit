package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.item.dto.ItemDtoOwnReq;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOwn;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createBooking() throws Exception {
        Long userId = 1L;
        ItemRequestDto reqItemRequestDto = ItemRequestDto.builder()
                .description("Хотел бы воспользоваться щёткой для обуви").build();
        ItemRequestDto resItemRequestDto = ItemRequestDto.builder().id(1L)
                .description("Хотел бы воспользоваться щёткой для обуви")
                .requestor(UserDto.builder().id(1L).name("user").email("user@user.com").build())
                .created(LocalDateTime.of(2023, 12, 3, 17, 0)).build();

        Mockito.when(itemRequestService.addRequest(eq(userId), any(ItemRequestDto.class)))
                .thenReturn(resItemRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(reqItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(resItemRequestDto)));

        Mockito.verify(itemRequestService, Mockito.times(1)).addRequest(eq(userId),
                any(ItemRequestDto.class));
        Mockito.verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void findItemRequestsByUser() throws Exception {
        Long userId = 1L;
        List<ItemRequestDtoOwn> itemRequestsDtoOwn = Collections.singletonList(ItemRequestDtoOwn.builder().id(1L)
                .description("Хотел бы воспользоваться щёткой для обуви")
                .created(LocalDateTime.of(2023, 12, 3, 17, 0))
                .items(Collections.singletonList(ItemDtoOwnReq.builder().id(5L)
                        .name("Щётка для обуви").description("Стандартная щётка для обуви").available(true)
                        .requestId(1L).build()))
                .build());

        Mockito.when(itemRequestService.findItemRequestsByUser(userId)).thenReturn(itemRequestsDtoOwn);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemRequestsDtoOwn)));

        Mockito.verify(itemRequestService, Mockito.times(1)).findItemRequestsByUser(userId);
        Mockito.verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void findItemRequestsByAnotherUser() throws Exception {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 2;
        List<ItemRequestDtoOwn> itemRequestsDtoOwn = Collections.singletonList(ItemRequestDtoOwn.builder().id(1L)
                .description("Хотел бы воспользоваться щёткой для обуви")
                .created(LocalDateTime.of(2023, 12, 3, 17, 0))
                .items(Collections.singletonList(ItemDtoOwnReq.builder().id(5L)
                        .name("Щётка для обуви").description("Стандартная щётка для обуви").available(true)
                        .requestId(1L).build()))
                .build());

        Mockito.when(itemRequestService.findItemRequestsByAnotherUser(userId, from, size)).thenReturn(itemRequestsDtoOwn);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemRequestsDtoOwn)));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .findItemRequestsByAnotherUser(userId, from, size);
        Mockito.verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getItemRequestById() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;
        Integer from = 0;
        Integer size = 2;
        ItemRequestDtoOwn itemRequestDtoOwn = ItemRequestDtoOwn.builder().id(requestId)
                .description("Хотел бы воспользоваться щёткой для обуви")
                .created(LocalDateTime.of(2023, 12, 3, 17, 0))
                .items(Collections.singletonList(ItemDtoOwnReq.builder().id(5L)
                        .name("Щётка для обуви").description("Стандартная щётка для обуви").available(true)
                        .requestId(1L).build()))
                .build();

        Mockito.when(itemRequestService.getItemRequestById(userId, requestId)).thenReturn(itemRequestDtoOwn);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/" + requestId)
                        .header("X-Sharer-User-Id", userId)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemRequestDtoOwn)));

        Mockito.verify(itemRequestService, Mockito.times(1)).getItemRequestById(userId,
                requestId);
        Mockito.verifyNoMoreInteractions(itemRequestService);
    }
}
