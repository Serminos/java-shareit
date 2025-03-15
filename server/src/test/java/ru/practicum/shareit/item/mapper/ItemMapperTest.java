package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.dto.ItemResponce;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ItemMapperTest {

    private final ItemMapper mapper = new ItemMapper();

    @Test
    void shouldMapToItemWithRequest() {
        ItemDto dto = new ItemDto();
        dto.setRequestId(1L);
        Request request = new Request();

        Item item = mapper.toItem(dto);
        item.setRequest(request);

        assertSame(request, item.getRequest());
    }

    @Test
    void shouldMapToResponseWithComments() {
        Item item = new Item();
        List<CommentDto> comments = List.of(new CommentDto());

        ItemResponce response = mapper.toItemResponce(item, comments);

        assertEquals(1, response.getComments().size());
    }

    @Test
    void shouldMaptoItemDtoForRequest() {
        Item item = new Item();
        item.setId(12L);
        item.setOwner(new User());
        ItemDtoForRequest itemDtoForRequest = mapper.toItemDtoForRequest(item);

        assertEquals(itemDtoForRequest.getItemId(), item.getId());
    }
}
