package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING,
        uses = {ItemMapper.class, UserMapper.class})
public interface CommentMapper {

    @Mapping(source = "author.name", target = "authorName")
    CommentDto toCommentDto(Comment comment);

    @Mapping(source = "commentDto.id", target = "id")
    @Mapping(source = "commentDto.text", target = "text")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "user", target = "author")
    @Mapping(source = "created", target = "created")
    Comment toComment(CommentDto commentDto, Item item, User user, LocalDateTime created);
}
