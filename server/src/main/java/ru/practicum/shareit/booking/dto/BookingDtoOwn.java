package ru.practicum.shareit.booking.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BookingDtoOwn {

    private Long id;

    private Long bookerId;
}
