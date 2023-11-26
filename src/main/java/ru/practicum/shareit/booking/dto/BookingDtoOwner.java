package ru.practicum.shareit.booking.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BookingDtoOwner {

    private Long id;

    private Long bookerId;
}
