package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDtoOwnReq;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOwn;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING,
        uses = {UserMapper.class, ItemMapper.class})
public interface ItemRequestMapper {

    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    ItemRequestDtoOwn toItemRequestDtoOwn(ItemRequest itemRequest, List<ItemDtoOwnReq> items);
}
