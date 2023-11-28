package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingDtoRequest bookingDtoRequest, Long userId);

    BookingDto approveOrRejectBooking(Long bookingId, Boolean approved, Long userId);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getAllBookingsByUserId(String state, Long userId);

    List<BookingDto> getAllBookingsForAllItemsByUserId(String state, Long userId);
}
