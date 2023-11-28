package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOwner;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING,
        uses = {ItemMapper.class, UserMapper.class})
public interface BookingMapper {

    BookingDto toBookingDto(Booking booking);

    @Mapping(source = "booker.id", target = "bookerId")
    BookingDtoOwner toBookingDtoOwner(Booking booking);

    Booking toBooking(BookingDto bookingDto);
}
