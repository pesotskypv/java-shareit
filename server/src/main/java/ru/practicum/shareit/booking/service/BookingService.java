package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoReq;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long userId, BookingDtoReq bookingDtoReq);

    BookingDto approveOrRejectBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getAllBookingsByUserId(Long userId, String state);

    List<BookingDto> getAllBookingsForAllItemsByUserId(Long userId, String state);
}
