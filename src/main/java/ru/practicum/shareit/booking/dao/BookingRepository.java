package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long userId, LocalDateTime now,
                                                                              LocalDateTime now1);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus bookingStatus);

    @Query("SELECT b " +
            "FROM Booking b " +
            "LEFT JOIN Item i ON b.item = i.id " +
            "LEFT JOIN User u ON i.owner = u.id " +
            "WHERE u.id = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllBookingsForAllItemsByUserId(Long userId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "LEFT JOIN Item i ON b.item = i.id " +
            "LEFT JOIN User u ON i.owner = u.id " +
            "WHERE u.id = ?1 AND ?2 BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsForAllItemsByUserId(Long userId, LocalDateTime now);

    @Query("SELECT b " +
            "FROM Booking b " +
            "LEFT JOIN Item i ON b.item = i.id " +
            "LEFT JOIN User u ON i.owner = u.id " +
            "WHERE u.id = ?1 AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findPastBookingsForAllItemsByUserId(Long userId, LocalDateTime now);

    @Query("SELECT b " +
            "FROM Booking b " +
            "LEFT JOIN Item i ON b.item = i.id " +
            "LEFT JOIN User u ON i.owner = u.id " +
            "WHERE u.id = ?1 AND b.start > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureBookingsForAllItemsByUserId(Long userId, LocalDateTime now);

    @Query("SELECT b " +
            "FROM Booking b " +
            "LEFT JOIN Item i ON b.item = i.id " +
            "LEFT JOIN User u ON i.owner = u.id " +
            "WHERE u.id = ?1 AND b.status = ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findStatusBookingsForAllItemsByUserId(Long userId, BookingStatus bookingStatus);

    Booking findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime now,
                                                                     BookingStatus bookingStatus);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now,
                                                                   BookingStatus bookingStatus);

    Booking findFirstByBookerIdAndItemIdAndStatusAndEndBeforeOrderByEndDesc(Long userId, Long itemId,
                                                                            BookingStatus bookingStatus,
                                                                            LocalDateTime now);
}
