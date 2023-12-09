package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwn;
import ru.practicum.shareit.item.dto.ItemDtoReq;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    @Test
    void addItem() throws Exception {
        Long userId = 1L;
        ItemDtoReq itemDto = ItemDtoReq.builder().name("Дрель").description("Простая дрель")
                .available(true).build();
        ItemDto savedItemDto = ItemDto.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                .owner(UserDto.builder().id(userId).name("user").email("user@user.com").build()).requestId(null)
                .build();

        Mockito.when(itemService.createItem(eq(userId), any(ItemDtoReq.class))).thenReturn(savedItemDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedItemDto)));

        Mockito.verify(itemService, Mockito.times(1)).createItem(eq(userId),
                any(ItemDtoReq.class));
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDtoOwn itemDtoOwn = ItemDtoOwn.builder().id(itemId).name("Дрель").description("Простая дрель")
                .available(true).owner(UserDto.builder().id(userId).name("user").email("user@user.com").build())
                .request(null).lastBooking(null).nextBooking(null).comments(new ArrayList<>()).build();

        Mockito.when(itemService.getItem(userId, itemId)).thenReturn(itemDtoOwn);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemDtoOwn)));

        Mockito.verify(itemService, Mockito.times(1)).getItem(userId, itemId);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void findItemsByUser() throws Exception {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 5;
        List<ItemDtoOwn> itemsDtoOwner = Collections.singletonList(ItemDtoOwn.builder().id(1L).name("Дрель")
                .description("Простая дрель").available(true)
                .owner(UserDto.builder().id(userId).name("user").email("user@user.com").build())
                .request(null).lastBooking(null).nextBooking(null).comments(new ArrayList<>()).build());

        Mockito.when(itemService.findItemsByUser(userId)).thenReturn(itemsDtoOwner);

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemsDtoOwner)));

        Mockito.verify(itemService, Mockito.times(1)).findItemsByUser(userId);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void searchItems() throws Exception {
        Long userId = 1L;
        String text = "дрель";
        Integer from = 0;
        Integer size = 5;
        List<ItemDto> itemsDto = Collections.singletonList(ItemDto.builder().id(1L).name("Дрель")
                .description("Простая дрель").available(true)
                .owner(UserDto.builder().id(userId).name("user").email("user@user.com").build())
                .requestId(null).build());

        Mockito.when(itemService.findItemsByText(userId, text)).thenReturn(itemsDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemsDto)));

        Mockito.verify(itemService, Mockito.times(1)).findItemsByText(userId, text);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void editItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto reqItemDto = ItemDto.builder().available(true).build();
        ItemDto resItemDto = ItemDto.builder().id(1L).name("Дрель")
                .description("Простая дрель").available(true)
                .owner(UserDto.builder().id(userId).name("user").email("user@user.com").build())
                .requestId(null).build();

        Mockito.when(itemService.updateItem(eq(userId), eq(itemId), any(ItemDto.class))).thenReturn(resItemDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(reqItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(resItemDto)));

        Mockito.verify(itemService, Mockito.times(1)).updateItem(eq(userId), eq(itemId),
                any(ItemDto.class));
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void addComment() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto reqCommentDto = CommentDto.builder().text("Comment").build();
        CommentDto resCommentDto = CommentDto.builder().id(1L).text("Comment").authorName("user")
                .created(LocalDateTime.of(2023, 12, 2, 16, 15)).build();

        Mockito.when(itemService.addComment(eq(userId), eq(itemId), any(CommentDto.class))).thenReturn(resCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/" + itemId + "/comment")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(reqCommentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(resCommentDto)));

        Mockito.verify(itemService, Mockito.times(1)).addComment(eq(userId), eq(itemId),
                any(CommentDto.class));
        Mockito.verifyNoMoreInteractions(itemService);
    }
}
