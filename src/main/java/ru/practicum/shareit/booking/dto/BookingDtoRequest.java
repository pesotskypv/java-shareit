package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BookingDtoRequest {

    private Long ItemId;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;
}
