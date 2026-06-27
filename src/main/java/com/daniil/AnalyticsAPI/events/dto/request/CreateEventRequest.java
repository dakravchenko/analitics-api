package com.daniil.AnalyticsAPI.events.dto.request;

import java.util.UUID;

import com.daniil.AnalyticsAPI.enums.EventSource;
import com.daniil.AnalyticsAPI.enums.EventType;

import jakarta.validation.constraints.NotNull;

public record CreateEventRequest(
        @NotNull(message = "eventType is required") EventType eventType,
        @NotNull(message = "eventSource is required") EventSource eventSource,
        @NotNull(message = "sessionId is required") UUID sessionId) {
}
