package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoOwnReq;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.CommentMapperImpl;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOwn;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@ContextConfiguration(classes = {UserMapperImpl.class, ItemMapperImpl.class, ItemRequestServiceImpl.class,
        ItemRequestMapperImpl.class, CommentMapperImpl.class})
public class ItemRequestServiceImplTest {

    @MockBean
    private UserRepository userRepository;
    @SpyBean
    private UserMapper userMapper;
    @MockBean
    private ItemRepository itemRepository;
    @SpyBean
    private ItemMapper itemMapper;
    @MockBean
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRequestService itemRequestService;
    @SpyBean
    private ItemRequestMapper itemRequestMapper;
    @SpyBean
    private CommentMapper commentMapper;

    @Test
    void addRequest_returnsItemRequestDtoWhenAdded() {
        Long userId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);
        ItemRequestDto inItemRequestDto = ItemRequestDto.builder().description("Хотел бы воспользоваться Дрелью")
                .build();
        User originalUser = User.builder().id(userId).name("user").email("user@user.com").build();
        ItemRequest inItemRequest = ItemRequest.builder().description(inItemRequestDto.getDescription())
                .requestor(originalUser).created(today).build();
        ItemRequest outItemRequest = ItemRequest.builder().id(1L).description(inItemRequestDto.getDescription())
                .requestor(originalUser).created(today).build();
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder().id(1L)
                .description("Хотел бы воспользоваться Дрелью").requestor(UserDto.builder().id(originalUser.getId())
                        .name(originalUser.getName()).email(originalUser.getEmail()).build()).created(today).build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(originalUser));
        Mockito.when(itemRequestRepository.save(inItemRequest)).thenReturn(outItemRequest);

        ItemRequestDto actualItemRequestDto;
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class,
                Mockito.CALLS_REAL_METHODS)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(today);

            actualItemRequestDto = itemRequestService.addRequest(userId, inItemRequestDto);
        }

        Assertions.assertEquals(expectedItemRequestDto.getId(), actualItemRequestDto.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).save(inItemRequest);
        Mockito.verifyNoMoreInteractions(itemRequestRepository);
        Mockito.verify(itemRequestMapper, Mockito.times(1)).toItemRequestDto(outItemRequest);
        Mockito.verifyNoMoreInteractions(itemRequestMapper);
    }

    @Test
    void findItemRequestsByUser_returnsListItemRequestDtoOwnWhenFound() {
        Long userId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        ItemRequest originalItemRequest = ItemRequest.builder().id(1L).description("Хотел бы воспользоваться Дрелью")
                .requestor(User.builder().id(userId).name("user").email("user@user.com").build())
                .created(today.minusDays(1)).build();
        List<ItemRequest> originalItemRequests = Collections.singletonList(originalItemRequest);

        Item originalItem = Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                .owner(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .request(originalItemRequest).build();
        List<Item> originalItems = Collections.singletonList(originalItem);

        List<ItemDtoOwnReq> originalItemsDtoOwnReq = Collections.singletonList(ItemDtoOwnReq.builder()
                .id(originalItem.getId()).name(originalItem.getName()).description(originalItem.getDescription())
                .available(originalItem.getAvailable()).requestId(originalItem.getRequest().getId()).build());

        ItemRequestDtoOwn outItemRequestDtoOwn = ItemRequestDtoOwn.builder().id(originalItemRequest.getId())
                .description(originalItemRequest.getDescription()).created(originalItemRequest.getCreated())
                .items(originalItemsDtoOwnReq).build();
        List<ItemRequestDtoOwn> expectedItemRequestDtoOwn = Collections.singletonList(outItemRequestDtoOwn);

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRequestRepository.findRequestsByRequestorId(userId)).thenReturn(originalItemRequests);
        Mockito.when(itemRepository.findItemsByRequestId(originalItemRequest.getId())).thenReturn(originalItems);

        List<ItemRequestDtoOwn> actualItemRequestsDtoOwn = itemRequestService.findItemRequestsByUser(userId);

        Assertions.assertEquals(expectedItemRequestDtoOwn.get(0).getId(), actualItemRequestsDtoOwn.get(0).getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findRequestsByRequestorId(userId);
        Mockito.verifyNoMoreInteractions(itemRequestRepository);
        Mockito.verify(itemRepository, Mockito.times(1))
                .findItemsByRequestId(originalItemRequest.getId());
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verify(itemMapper, Mockito.times(1)).toItemDtoItemRequestOwn(originalItem);
        Mockito.verifyNoMoreInteractions(itemMapper);
    }

    @Test
    void findItemRequestsByUser_returnsEntityNotFoundException() {
        Long userId = 1L;

        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                itemRequestService.findItemRequestsByUser(userId));

        assertTrue(exception.getMessage().contains("Попытка получить список запросов вещей несуществующим " +
                "пользователем"));
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findItemRequestsByAnotherUser_returnsListItemRequestDtoOwnWhenFound() {
        Long userId = 1L;
        Integer offset = 0;
        Integer limit = 2;
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        ItemRequest originalItemRequest = ItemRequest.builder().id(2L).description("Хотел бы воспользоваться Дрелью")
                .requestor(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .created(today.minusDays(1)).build();
        ItemRequest originalItemRequest2 = ItemRequest.builder().id(1L).description("Хотел бы воспользоваться щёткой")
                .requestor(User.builder().id(userId).name("user").email("user@user.com").build())
                .created(today.minusDays(2)).build();
        List<ItemRequest> originalItemRequests = List.of(originalItemRequest, originalItemRequest2);
        Page<ItemRequest> originalItemRequestsPag = new PageImpl<>(originalItemRequests,
                PageRequest.of(offset, limit, Sort.by("created").descending()), originalItemRequests.size());

        Item originalItem = Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                .owner(User.builder().id(3L).name("user3").email("user3@user.com").build())
                .request(originalItemRequest).build();
        List<Item> originalItems = Collections.singletonList(originalItem);

        List<ItemDtoOwnReq> originalItemsDtoOwnReq = Collections.singletonList(ItemDtoOwnReq.builder()
                .id(originalItem.getId()).name(originalItem.getName()).description(originalItem.getDescription())
                .available(originalItem.getAvailable()).requestId(originalItem.getRequest().getId()).build());

        ItemRequestDtoOwn outItemRequestDtoOwn = ItemRequestDtoOwn.builder().id(originalItemRequest.getId())
                .description(originalItemRequest.getDescription()).created(originalItemRequest.getCreated())
                .items(originalItemsDtoOwnReq).build();
        List<ItemRequestDtoOwn> expectedItemRequestDtoOwn = Collections.singletonList(outItemRequestDtoOwn);

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRequestRepository.findAll(PageRequest.of(offset, limit, Sort.by("created")
                .descending()))).thenReturn(originalItemRequestsPag);
        Mockito.when(itemRepository.findItemsByRequestId(originalItemRequest.getId())).thenReturn(originalItems);

        List<ItemRequestDtoOwn> actualItemRequestsDtoOwn = itemRequestService.findItemRequestsByAnotherUser(userId,
                offset, limit);

        Assertions.assertEquals(expectedItemRequestDtoOwn.get(0).getId(), actualItemRequestsDtoOwn.get(0).getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAll(PageRequest.of(offset, limit, Sort.by("created").descending()));
        Mockito.verifyNoMoreInteractions(itemRequestRepository);
        Mockito.verify(itemRepository, Mockito.times(1))
                .findItemsByRequestId(originalItemRequest.getId());
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verify(itemMapper, Mockito.times(1)).toItemDtoItemRequestOwn(originalItem);
        Mockito.verifyNoMoreInteractions(itemMapper);
    }

    @Test
    void findItemRequestsByAnotherUser_returnsEntityNotFoundException() {
        Long userId = 1L;
        Integer offset = 0;
        Integer limit = 2;

        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                itemRequestService.findItemRequestsByAnotherUser(userId, offset, limit));

        assertTrue(exception.getMessage().contains("Попытка получить список запросов вещей несуществующим " +
                "пользователем"));
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getItemRequestById_returnsListItemRequestDtoOwnWhenFound() {
        Long userId = 1L;
        Long requestId = 1L;
        LocalDateTime today = LocalDateTime.of(2023, 12, 2, 17, 0);

        ItemRequest originalItemRequest = ItemRequest.builder().id(requestId)
                .description("Хотел бы воспользоваться Дрелью").requestor(User.builder().id(userId).name("user")
                        .email("user@user.com").build())
                .created(today.minusDays(1)).build();

        Item originalItem = Item.builder().id(1L).name("Дрель").description("Простая дрель").available(true)
                .owner(User.builder().id(2L).name("user2").email("user2@user.com").build())
                .request(originalItemRequest).build();
        List<Item> originalItems = Collections.singletonList(originalItem);

        List<ItemDtoOwnReq> originalItemsDtoOwnReq = Collections.singletonList(ItemDtoOwnReq.builder()
                .id(originalItem.getId()).name(originalItem.getName()).description(originalItem.getDescription())
                .available(originalItem.getAvailable()).requestId(originalItem.getRequest().getId()).build());

        ItemRequestDtoOwn expectedItemRequestDtoOwn = ItemRequestDtoOwn.builder().id(originalItemRequest.getId())
                .description(originalItemRequest.getDescription()).created(originalItemRequest.getCreated())
                .items(originalItemsDtoOwnReq).build();

        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(originalItemRequest));
        Mockito.when(itemRepository.findItemsByRequestId(requestId)).thenReturn(originalItems);

        ItemRequestDtoOwn actualItemRequestsDtoOwn = itemRequestService.getItemRequestById(userId, requestId);

        Assertions.assertEquals(expectedItemRequestDtoOwn.getId(), actualItemRequestsDtoOwn.getId());
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findById(requestId);
        Mockito.verifyNoMoreInteractions(itemRequestRepository);
        Mockito.verify(itemRepository, Mockito.times(1))
                .findItemsByRequestId(requestId);
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verify(itemMapper, Mockito.times(1)).toItemDtoItemRequestOwn(originalItem);
        Mockito.verifyNoMoreInteractions(itemMapper);
    }

    @Test
    void getItemRequestById_returnsEntityNotFoundException() {
        Long userId = 1L;
        Long requestId = 1L;

        Mockito.when(userRepository.existsById(userId)).thenReturn(false);

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                itemRequestService.getItemRequestById(userId, requestId));

        assertTrue(exception.getMessage().contains("Попытка получить список запросов вещей несуществующим " +
                "пользователем"));
        Mockito.verify(userRepository, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoMoreInteractions(userRepository);
    }
}
