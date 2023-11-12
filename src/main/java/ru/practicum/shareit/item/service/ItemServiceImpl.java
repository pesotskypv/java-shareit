package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exception.UserForbiddenException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User user = userRepository.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException("Попытка добавить вещь несуществующим владельцем"));
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);

        return itemMapper.toItemDto(itemRepository.addItem(item));
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        return itemMapper.toItemDto(itemRepository.getItem(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Попытка получить несуществующую вещь")));
    }

    @Override
    public List<ItemDto> findItemsByUser(Long userId) {
        return itemRepository.findItemsByUser(userId).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItemsByText(String text, Long userId) {
        userRepository.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException("Попытка поиска вещи несуществующим пользователем"));

        return itemRepository.findItemsByText(text).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        userRepository.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException("Попытка отредактировать вещь несуществующим владельцем"));
        Item itemStorage = itemRepository.getItem(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Попытка отредактировать несуществующую вещь"));

        if (!Objects.equals(userId, itemStorage.getOwner().getId())) {
            String textError = "Попытка отредактировать вещь пользователем не являющимся её владельцем";

            log.debug(textError);
            throw new UserForbiddenException(textError);
        }

        String name = itemDto.getName();
        String description = itemDto.getDescription();

        if (!Objects.equals(itemDto.getId(), itemId)) {
            itemDto.setId(itemId);
        }
        if (name != null && name.isBlank()) {
            String textError = "Название вещи не может быть пустым";

            log.debug("Валидация не пройдена: " + textError);
            throw new ItemValidationException(textError);
        }
        if (description != null && description.isBlank()) {
            String textError = "Описание вещи не может быть пустым";

            log.debug("Валидация не пройдена: " + textError);
            throw new ItemValidationException(textError);
        }

        return itemMapper.toItemDto(itemRepository.updateItem(itemMapper.toItem(itemDto)));
    }
}