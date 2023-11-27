package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.EntityValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(BookingDtoRequest bookingDtoRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Попытка аренды вещи несуществующим пользователем"));
        Item item = itemRepository.findById(bookingDtoRequest.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Попытка арендовать несуществующую вещь"));
        LocalDateTime start = bookingDtoRequest.getStart();
        LocalDateTime end = bookingDtoRequest.getEnd();

        if (userId.equals(item.getOwner().getId()))
            throw new EntityNotFoundException("Нельзя арендовать собсьвенную вещь");

        if (!item.getAvailable())
            throw new EntityValidationException("Вещь не доступна для аренды");

        if (start.isBefore(LocalDateTime.now()) || start.equals(end) || start.isAfter(end))
            throw new EntityValidationException("Некорректный период аренды");

        return bookingMapper.toBookingDto(bookingRepository.save(Booking.builder().start(start).end(end).item(item)
                .booker(user).status(BookingStatus.WAITING).build()));
    }

    @Override
    public BookingDto approveOrRejectBooking(Long bookingId, Boolean approved, Long userId) {
        if(!userRepository.existsById(userId))
            throw new EntityNotFoundException("Попытка подтверждения или отклонения запроса на бронирование " +
                    "несуществующим пользователем");

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Попытка подтверждения или отклонения несуществующего " +
                        "запроса на бронирование"));

        if (!booking.getItem().getOwner().getId().equals(userId))
            throw new EntityNotFoundException("Пользователь не является владельцем вещи");

        if (booking.getStatus().equals(BookingStatus.WAITING)) {
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new EntityValidationException("Попытка подтверждения или отклонения запроса находящегося не в " +
                    "статусе «ожидает подтверждения»");
        }

        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        if(!userRepository.existsById(userId))
            throw new EntityNotFoundException("Попытка получения данных о бронировании несуществующим пользователем");

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Попытка получения данных о несуществующем " +
                        "бронировании"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId))
            throw new EntityNotFoundException("Пользователь не является автором бронирования или владельцем вещи");

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsByUserId(String state, Long userId) {
        if(!userRepository.existsById(userId))
            throw new EntityNotFoundException("Попытка получения данных о бронировании несуществующим пользователем");

        switch (state) {
            case "ALL":
                return bookingRepository.findByBookerIdOrderByStartDesc(userId).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now()).stream().map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now())
                        .stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now())
                        .stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED)
                        .stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());
            default:
                throw new EntityValidationException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public List<BookingDto> getAllBookingsForAllItemsByUserId(String state, Long userId) {
        if(!userRepository.existsById(userId))
            throw new EntityNotFoundException("Попытка получения данных о бронировании несуществующим пользователем");

        switch (state) {
            case "ALL":
                return bookingRepository.findAllBookingsForAllItemsByUserId(userId).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findCurrentBookingsForAllItemsByUserId(userId, LocalDateTime.now()).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findPastBookingsForAllItemsByUserId(userId, LocalDateTime.now()).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findFutureBookingsForAllItemsByUserId(userId, LocalDateTime.now()).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findStatusBookingsForAllItemsByUserId(userId, BookingStatus.WAITING).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findStatusBookingsForAllItemsByUserId(userId, BookingStatus.REJECTED).stream()
                        .map(bookingMapper::toBookingDto).collect(Collectors.toList());
            default:
                throw new EntityValidationException(String.format("Unknown state: %s", state));
        }
    }
}
