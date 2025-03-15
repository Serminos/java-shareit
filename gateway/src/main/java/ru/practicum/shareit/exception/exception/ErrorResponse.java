package ru.practicum.shareit.exception.exception;

import java.util.Map;

public class ErrorResponse {
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public ErrorResponse(Map<String, String> error) {
        StringBuilder sb = new StringBuilder();
        boolean first = true; // Track if it's the first entry

        for (Map.Entry<String, String> entry : error.entrySet()) {
            if (!first) {
                sb.append(";");
            }
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            first = false;
        }
        this.error = sb.toString();
    }

    public String getError() {
        return error;
    }
}
