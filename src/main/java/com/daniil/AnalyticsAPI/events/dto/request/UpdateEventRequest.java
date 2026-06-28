package com.daniil.AnalyticsAPI.events.dto.request;

import com.daniil.AnalyticsAPI.enums.EventSource;
import com.daniil.AnalyticsAPI.enums.EventType;
import jakarta.validation.constraints.NotNull;

public record UpdateEventRequest(
    @NotNull(message = "eventType is required")
    EventType eventType,
    
    @NotNull(message = "eventSource is required")
    EventSource eventSource
) {}