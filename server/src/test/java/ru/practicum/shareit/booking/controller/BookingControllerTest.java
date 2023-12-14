package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoReq;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    @Test
    void createBooking() throws Exception {
        Long userId = 1L;
        BookingDtoReq bookingDtoReq = BookingDtoReq.builder().itemId(3L)
                .start(LocalDateTime.of(2023, 12, 3, 17, 0))
                .end(LocalDateTime.of(2023, 12, 4, 17, 0)).build();
        BookingDto bookingDto = BookingDto.builder().id(5L)
                .start(LocalDateTime.of(2023, 12, 3, 17, 0))
                .end(LocalDateTime.of(2023, 12, 4, 17, 0))
                .item(ItemDto.builder().id(3L).name("Клей Момент").description("Тюбик суперклея марки Момент")
                        .available(true)
                        .owner(UserDto.builder().id(4L).name("user").email("user@user.com").build())
                        .requestId(null).build())
                .booker(UserDto.builder().id(userId).name("updateName").email("updateName@user.com").build())
                .status(BookingStatus.WAITING).build();

        Mockito.when(bookingService.createBooking(eq(userId), any(BookingDtoReq.class))).thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingDtoReq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingDto)));

        Mockito.verify(bookingService, Mockito.times(1)).createBooking(eq(userId),
                any(BookingDtoReq.class));
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void approveOrRejectBooking() throws Exception {
        Long userId = 4L;
        Long bookingId = 5L;
        Boolean approved = false;
        BookingDto bookingDto = BookingDto.builder().id(bookingId)
                .start(LocalDateTime.of(2023, 12, 3, 17, 0))
                .end(LocalDateTime.of(2023, 12, 4, 17, 0))
                .item(ItemDto.builder().id(3L).name("Клей Момент").description("Тюбик суперклея марки Момент")
                        .available(true)
                        .owner(UserDto.builder().id(userId).name("user").email("user@user.com").build())
                        .requestId(null).build())
                .booker(UserDto.builder().id(1L).name("updateName").email("updateName@user.com").build())
                .status(BookingStatus.REJECTED).build();

        Mockito.when(bookingService.approveOrRejectBooking(userId, bookingId, approved)).thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", approved.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingDto)));

        Mockito.verify(bookingService, Mockito.times(1)).approveOrRejectBooking(userId,
                bookingId, approved);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingById() throws Exception {
        Long userId = 1L;
        Long bookingId = 5L;
        BookingDto bookingDto = BookingDto.builder().id(bookingId)
                .start(LocalDateTime.of(2023, 12, 3, 17, 0))
                .end(LocalDateTime.of(2023, 12, 4, 17, 0))
                .item(ItemDto.builder().id(3L).name("Клей Момент").description("Тюбик суперклея марки Момент")
                        .available(true)
                        .owner(UserDto.builder().id(4L).name("user").email("user@user.com").build())
                        .requestId(null).build())
                .booker(UserDto.builder().id(userId).name("updateName").email("updateName@user.com").build())
                .status(BookingStatus.REJECTED).build();

        Mockito.when(bookingService.getBookingById(userId, bookingId)).thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingDto)));

        Mockito.verify(bookingService, Mockito.times(1)).getBookingById(userId, bookingId);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getAllBookingsByUserId() throws Exception {
        Long userId = 1L;
        Long bookingId = 5L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 2;
        List<BookingDto> bookingsDto = Collections.singletonList(BookingDto.builder().id(bookingId)
                .start(LocalDateTime.of(2023, 12, 3, 17, 0))
                .end(LocalDateTime.of(2023, 12, 4, 17, 0))
                .item(ItemDto.builder().id(3L).name("Клей Момент").description("Тюбик суперклея марки Момент")
                        .available(true)
                        .owner(UserDto.builder().id(4L).name("user").email("user@user.com").build())
                        .requestId(null).build())
                .booker(UserDto.builder().id(userId).name("updateName").email("updateName@user.com").build())
                .status(BookingStatus.REJECTED).build());

        Mockito.when(bookingService.getAllBookingsByUserId(userId, state)).thenReturn(bookingsDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingsDto)));

        Mockito.verify(bookingService, Mockito.times(1)).getAllBookingsByUserId(userId, state);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getAllBookingsForAllItemsByUserId() throws Exception {
        Long userId = 4L;
        Long bookingId = 5L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 2;
        List<BookingDto> bookingsDto = Collections.singletonList(BookingDto.builder().id(bookingId)
                .start(LocalDateTime.of(2023, 12, 3, 17, 0))
                .end(LocalDateTime.of(2023, 12, 4, 17, 0))
                .item(ItemDto.builder().id(3L).name("Клей Момент").description("Тюбик суперклея марки Момент")
                        .available(true)
                        .owner(UserDto.builder().id(userId).name("user").email("user@user.com").build())
                        .requestId(null).build())
                .booker(UserDto.builder().id(1L).name("updateName").email("updateName@user.com").build())
                .status(BookingStatus.REJECTED).build());

        Mockito.when(bookingService.getAllBookingsForAllItemsByUserId(userId, state)).thenReturn(bookingsDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingsDto)));

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllBookingsForAllItemsByUserId(userId, state);
        Mockito.verifyNoMoreInteractions(bookingService);
    }
}
