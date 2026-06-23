package com.daniil.AnalyticsAPI.events.models;

import java.time.LocalDateTime;
import java.util.UUID;


import com.daniil.AnalyticsAPI.enums.EventSource;
import com.daniil.AnalyticsAPI.enums.EventType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString.Exclude;

@Getter
@Setter
@AllArgsConstructor
public class EventModel {
    private UUID traceId;
    private LocalDateTime timestamp;
    private EventType eventType;
    private EventSource eventSource;
    @Exclude
    private UUID sessionId;
}
