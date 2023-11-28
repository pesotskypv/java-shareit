package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwner;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING,
        uses = {UserMapper.class, ItemRequestMapper.class, BookingMapper.class, CommentMapper.class})
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    @Mapping(source = "comments", target = "comments")
    ItemDtoOwner toItemDtoOwner(Item item, List<Comment> comments);

    Item toItem(ItemDto itemDto);
}
