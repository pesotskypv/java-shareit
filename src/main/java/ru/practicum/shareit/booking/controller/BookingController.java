package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoReq;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody BookingDtoReq bookingDtoReq,
                                    @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен POST-запрос /bookings с телом={} и userId={}", bookingDtoReq, userId);

        return bookingService.createBooking(bookingDtoReq, userId);
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
            @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Positive Integer size,
            @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен GET-запрос /bookings?state={}&from={}&size={} с userId={}", state, from, size, userId);

        List<BookingDto> bookingsDto = bookingService.getAllBookingsByUserId(state, userId);

        return bookingsDto.subList(from, Math.min((from + size), bookingsDto.size()));
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsForAllItemsByUserId(
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
            @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Positive Integer size,
            @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        log.info("Получен GET-запрос /bookings/owner?state={}&from={}&size={} с userId={}", state, from, size, userId);

        List<BookingDto> bookingsDto = bookingService.getAllBookingsForAllItemsByUserId(state, userId);

        return bookingsDto.subList(from, Math.min((from + size), bookingsDto.size()));
    }
}
