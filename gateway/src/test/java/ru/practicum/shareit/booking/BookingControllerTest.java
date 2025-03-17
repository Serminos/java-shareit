package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    private final String userIdHeader = "X-Sharer-User-Id";
    private final String basePath = "/bookings";

    @Test
    void createBooking_WithValidData_ReturnsCreated() throws Exception {
        // Подготовка данных
        BookItemRequestDto request = new BookItemRequestDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        when(bookingClient.saveRequest(anyLong(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        // Проверка
        mockMvc.perform(post(basePath)
                        .header(userIdHeader, 1)
                        .contentType("application/json")
                        .content("{\"itemId\":1," +
                                "\"start\":\"2030-01-01T12:00:00\"," +
                                "\"end\":\"2030-01-02T12:00:00\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void approveBooking_WithInvalidBookingId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(patch(basePath + "/-1")
                        .header(userIdHeader, 1)
                        .param("approved", "true"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getBookingById_WithValidIds_ReturnsOk() throws Exception {
        when(bookingClient.findById(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get(basePath + "/1")
                        .header(userIdHeader, 1))
                .andExpect(status().isOk());
    }

    @Test
    void getUserBookings_WithInvalidState_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get(basePath)
                        .header(userIdHeader, 1)
                        .param("state", "UNKNOWN"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getOwnerBookings_WithDefaultState_ReturnsAll() throws Exception {
        when(bookingClient.findAllByOwnerId(anyLong(), eq(BookingState.ALL)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get(basePath + "/owner")
                        .header(userIdHeader, 1))
                .andExpect(status().isOk());
    }

    @Test
    void createBooking_WithoutUserIdHeader_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post(basePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateBooking_WithInvalidUserId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(patch(basePath + "/1")
                        .header(userIdHeader, -1)
                        .param("approved", "false"))
                .andExpect(status().isInternalServerError());
    }
}