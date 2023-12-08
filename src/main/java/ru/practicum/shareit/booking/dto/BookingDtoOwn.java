package ru.practicum.shareit.booking.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoOwn {

    private Long id;

    private Long bookerId;
}
