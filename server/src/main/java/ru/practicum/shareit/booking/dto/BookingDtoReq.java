package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BookingDtoReq {

    private Long itemId;

    private LocalDateTime start;

    private LocalDateTime end;
}
