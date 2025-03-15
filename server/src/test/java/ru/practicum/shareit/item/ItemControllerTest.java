package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponce;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private static final long USER_ID = 1L;
    private static final long ITEM_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void addItem_ValidRequest_ReturnsCreated() throws Exception {
        ItemDto request = new ItemDto();
        request.setName("Item");
        request.setDescription("Description");
        request.setAvailable(true);

        ItemDto response = new ItemDto();
        response.setId(ITEM_ID);
        response.setName("Item");

        when(itemService.add(eq(USER_ID), any(ItemDto.class))).thenReturn(response);

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ITEM_ID))
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void updateItem_ValidRequest_ReturnsOk() throws Exception {
        ItemDto request = new ItemDto();
        request.setName("Updated Item");

        ItemDto response = new ItemDto();
        response.setId(ITEM_ID);
        response.setName("Updated Item");

        when(itemService.update(eq(USER_ID), eq(ITEM_ID), any(ItemDto.class))).thenReturn(response);

        mockMvc.perform(patch("/items/{itemId}", ITEM_ID)
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ITEM_ID))
                .andExpect(jsonPath("$.name").value("Updated Item"));
    }

    @Test
    void getItemById_ValidRequest_ReturnsOk() throws Exception {
        ItemResponce response = new ItemResponce();
        response.setId(ITEM_ID);
        response.setName("Item");

        when(itemService.findById(USER_ID, ITEM_ID)).thenReturn(response);

        mockMvc.perform(get("/items/{itemId}", ITEM_ID)
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ITEM_ID))
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void findAllByUserId_ValidRequest_ReturnsList() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(ITEM_ID);
        itemDto.setName("Item");

        when(itemService.findAllByUserId(USER_ID)).thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ITEM_ID));
    }

    @Test
    void findAllByText_ValidText_ReturnsList() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(ITEM_ID);
        itemDto.setName("Item");

        when(itemService.findAllByText("text")).thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ITEM_ID));
    }

    @Test
    void saveComment_ValidRequest_ReturnsCreated() throws Exception {
        CommentDto request = new CommentDto();
        request.setText("Comment text");

        CommentDto response = new CommentDto();
        response.setId(1L);
        response.setText("Comment text");

        when(itemService.saveComment(eq(USER_ID), eq(ITEM_ID), any(CommentDto.class))).thenReturn(response);

        mockMvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Comment text"));
    }


    @Test
    void shouldCreateItem() throws Exception {
        given(itemService.add(anyLong(), any()))
                .willReturn(new ItemDto());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Item\"}"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void shouldRejectCommentWithoutBooking() throws Exception {
        given(itemService.saveComment(anyLong(), anyLong(), any()))
                .willThrow(new BadRequestException("Error"));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"test\"}"))
                .andExpect(status().isBadRequest());
    }
}