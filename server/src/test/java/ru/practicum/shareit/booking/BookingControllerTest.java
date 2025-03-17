package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.StatusType;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    static final String USER_HEADER = "X-Sharer-User-Id";
    static final LocalDateTime NOW = LocalDateTime.now();

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookingService bookingService;

    BookingResponse bookingResponse;
    BookingRequest bookingRequest;

    @Test
    void shouldReturnCreatedStatus() throws Exception {
        given(bookingService.saveRequest(any(), anyLong()))
                .willReturn(new BookingResponse());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType("application/json")
                        .content("{\"itemId\":1,\"start\":\"2030-01-01T00:00\",\"end\":\"2030-01-02T00:00\"}"))
                .andExpect(status().isCreated());
    }

    @BeforeEach
    void setUp() {
        bookingResponse = new BookingResponse(
                1L,
                NOW.plusHours(1),
                NOW.plusDays(1),
                null,
                null,
                StatusType.WAITING
        );

        bookingRequest = new BookingRequest(

                NOW.plusHours(1),
                NOW.plusDays(1), 1L
        );
    }

    @Test
    void createBooking_ValidRequest_ReturnsCreated() throws Exception {
        when(bookingService.saveRequest(any(BookingRequest.class), anyLong()))
                .thenReturn(bookingResponse);

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approveBooking_ValidRequest_ReturnsOk() throws Exception {
        BookingResponse approvedResponse = new BookingResponse(
                bookingResponse.getId(),
                bookingResponse.getStart(),
                bookingResponse.getEnd(),
                bookingResponse.getItem(),
                bookingResponse.getBooker(),
                StatusType.APPROVED
        );

        when(bookingService.approved(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(approvedResponse);

        mockMvc.perform(patch("/bookings/1")
                        .header(USER_HEADER, 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingById_ValidRequest_ReturnsOk() throws Exception {
        when(bookingService.findById(anyLong(), anyLong()))
                .thenReturn(bookingResponse);

        mockMvc.perform(get("/bookings/1")
                        .header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUserBookings_ValidRequest_ReturnsList() throws Exception {
        when(bookingService.findAllByUserId(anyLong(), anyString()))
                .thenReturn(Collections.singletonList(bookingResponse));

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getOwnerBookings_ValidRequest_ReturnsList() throws Exception {
        when(bookingService.findAllByOwnerId(anyLong(), anyString()))
                .thenReturn(Collections.singletonList(bookingResponse));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}