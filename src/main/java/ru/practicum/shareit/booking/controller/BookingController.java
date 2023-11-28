package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody BookingDtoRequest bookingDtoRequest,
                                    @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен POST-запрос /bookings с телом={} и userId={}", bookingDtoRequest, userId);

        return bookingService.createBooking(bookingDtoRequest, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(@PathVariable @Positive Long bookingId,
                            @RequestParam(value = "approved") Boolean approved,
                            @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен PATCH-запрос /bookings/{}?approved={} с userId={}", bookingId, approved, userId);

        return bookingService.approveOrRejectBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable @Positive Long bookingId,
                                     @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен GET-запрос /bookings/{} с userId={}", bookingId, userId);

        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUserId(
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
            @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен GET-запрос /bookings?state={} с userId={}", state, userId);

        return bookingService.getAllBookingsByUserId(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsForAllItemsByUserId(
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
            @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен GET-запрос /bookings/owner?state={} с userId={}", state, userId);

        return bookingService.getAllBookingsForAllItemsByUserId(state, userId);
    }
}
