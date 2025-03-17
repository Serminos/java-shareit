package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    private final String userIdHeader = "X-Sharer-User-Id";
    private final String basePath = "/items";

    @Test
    void createItem_ValidData_ReturnsCreated() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("Скафандр")
                .description("Черный")
                .available(true).build();

        when(itemClient.add(anyLong(), any(ItemDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        // Act & Assert
        mockMvc.perform(post(basePath)
                        .header(userIdHeader, 1L)
                        .contentType("application/json")
                        .content("{\"name\":\"Скафандр\",\"description\":\"Черный\",\"available\":true}"))
                .andExpect(status().isCreated());
    }

    @Test
    void createItem_InvalidUserId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post(basePath)
                        .header(userIdHeader, -1L)
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_ValidData_ReturnsOk() throws Exception {
        when(itemClient.update(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(patch(basePath + "/1")
                        .header(userIdHeader, 1L)
                        .contentType("application/json")
                        .content("{\"name\":\"Новый Скафандр\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void getItemById_ValidIds_ReturnsOk() throws Exception {
        when(itemClient.getItemById(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get(basePath + "/1")
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItems_ValidUserId_ReturnsOk() throws Exception {
        when(itemClient.getItemsByOwnerId(anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get(basePath)
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems_ValidQuery_ReturnsOk() throws Exception {
        when(itemClient.search(anyString()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get(basePath + "/search?text=скафандр"))
                .andExpect(status().isOk());
    }

    @Test
    void createComment_ValidData_ReturnsCreated() throws Exception {
        when(itemClient.saveComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        mockMvc.perform(post(basePath + "/1/comment")
                        .header(userIdHeader, 1L)
                        .contentType("application/json")
                        .content("{\"text\":\"Привозите чаще!\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void createComment_EmptyText_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post(basePath + "/1/comment")
                        .header(userIdHeader, 1L)
                        .contentType("application/json")
                        .content("{\"text\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}