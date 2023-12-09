package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwnReq;
import ru.practicum.shareit.item.dto.ItemDtoOwn;
import ru.practicum.shareit.item.dto.ItemDtoReq;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING,
        uses = {UserMapper.class, ItemRequestMapper.class, BookingMapper.class, CommentMapper.class})
public interface ItemMapper {

    @Mapping(source = "item.request.id", target = "requestId")
    ItemDto toItemDto(Item item);

    @Mapping(source = "item.request.id", target = "requestId")
    ItemDtoOwnReq toItemDtoItemRequestOwn(Item item);

    @Mapping(source = "comments", target = "comments")
    ItemDtoOwn toItemDtoOwner(Item item, List<Comment> comments);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "itemDtoReq.name", target = "name")
    @Mapping(source = "user", target = "owner")
    Item toItem(ItemDtoReq itemDtoReq, User user);
}
