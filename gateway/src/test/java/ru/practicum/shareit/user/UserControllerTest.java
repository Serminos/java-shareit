package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    private final String basePath = "/users";

    @Test
    void createUser_ValidData_ReturnsCreated() throws Exception {
        when(userClient.save(any())).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        mockMvc.perform(post(basePath)
                        .contentType("application/json")
                        .content("{\"name\":\"Билл Гейтс\",\"email\":\"bg@co.ru\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void createUser_InvalidEmail_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post(basePath)
                        .contentType("application/json")
                        .content("{\"name\":\"Петя\",\"email\":\"Петя-инвалид-email\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_ValidPartialData_ReturnsOk() throws Exception {
        when(userClient.update(anyLong(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(patch(basePath + "/1")
                        .contentType("application/json")
                        .content("{\"email\":\"updated@test.ru\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_InvalidUserId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(patch(basePath + "/-1")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getUserById_ValidId_ReturnsOk() throws Exception {
        when(userClient.getUserById(anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get(basePath + "/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_InvalidId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get(basePath + "/0"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteUser_ValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete(basePath + "/111"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_InvalidId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete(basePath + "/-55"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createUser_EmptyName_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post(basePath)
                        .contentType("application/json")
                        .content("{\"email\":\"test@test.ru\"}"))
                .andExpect(status().isBadRequest());
    }
}