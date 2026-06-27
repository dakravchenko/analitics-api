package com.daniil.AnalyticsAPI.events.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.daniil.AnalyticsAPI.enums.EventSource;
import com.daniil.AnalyticsAPI.enums.EventType;

public record EventResponse(
    UUID traceId,
    LocalDateTime timestamp,
    EventType eventType,
    EventSource eventSource
) {}