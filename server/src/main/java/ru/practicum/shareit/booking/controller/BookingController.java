package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoReq;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody BookingDtoReq bookingDtoReq) {
        log.info("Получен POST-запрос /bookings с телом={} и userId={}", bookingDtoReq, userId);

        return bookingService.createBooking(userId, bookingDtoReq);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam(value = "approved") Boolean approved) {
        log.info("Получен PATCH-запрос /bookings/{}?approved={} с userId={}", bookingId, approved, userId);

        return bookingService.approveOrRejectBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {
        log.info("Получен GET-запрос /bookings/{} с userId={}", bookingId, userId);

        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL", required = false) String state,
            @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Получен GET-запрос /bookings?state={}&from={}&size={} с userId={}", state, from, size, userId);

        List<BookingDto> bookingsDto = bookingService.getAllBookingsByUserId(userId, state);

        return bookingsDto.subList(from, Math.min((from + size), bookingsDto.size()));
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsForAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL", required = false) String state,
            @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Получен GET-запрос /bookings/owner?state={}&from={}&size={} с userId={}", state, from, size, userId);

        List<BookingDto> bookingsDto = bookingService.getAllBookingsForAllItemsByUserId(userId, state);

        return bookingsDto.subList(from, Math.min((from + size), bookingsDto.size()));
    }
}
