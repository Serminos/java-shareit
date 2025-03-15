package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestResponseDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private RequestService requestService;

    @Test
    void saveRequest_ShouldReturn201() throws Exception {
        RequestResponseDto response = RequestResponseDto.builder().id(1L).build();
        given(requestService.save(anyLong(), any())).willReturn(response);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"test\"}"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAllByUserId_ShouldReturnList() throws Exception {
        given(requestService.getAllByUserId(anyLong()))
                .willReturn(List.of(new RequestResponseDto()));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findAllExceptUserId_ShouldReturnList() throws Exception {
        given(requestService.getAllByUserId(anyLong()))
                .willReturn(List.of(new RequestResponseDto()));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getByRequestId_ShouldReturn200() throws Exception {
        given(requestService.getByRequestId(anyLong()))
                .willReturn(new RequestResponseDto());

        mvc.perform(get("/requests/1"))
                .andExpect(status().isOk());
    }
}