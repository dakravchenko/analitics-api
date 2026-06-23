package com.daniil.AnalyticsAPI.events;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.daniil.AnalyticsAPI.events.models.EventModel;
import com.daniil.AnalyticsAPI.enums.EventSource;
import com.daniil.AnalyticsAPI.enums.EventType;
import com.daniil.AnalyticsAPI.events.exception.RecordNotFoundException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventsService {
    private List<EventModel> events = new ArrayList<>();

    public EventModel findEventById(UUID id) {
        return events.stream()
                .filter(event -> id.equals(event.getTraceId()))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException(
                        "Analytics record with id '" + id + "' not found"));
    }

    public List<EventModel> filterEvents(EventType eventType, EventSource eventSource, LocalDateTime startTime,
            LocalDateTime endTime) {
        return events.stream()
                .filter(event -> eventType == null || event.getEventType().equals(eventType))
                .filter(event -> eventSource == null || event.getEventSource().equals(eventSource))
                .filter(event -> startTime == null || event.getTimestamp().compareTo(startTime) >= 0)
                .filter(event -> endTime == null || event.getTimestamp().compareTo(endTime) <= 0)
                .toList();
    }
}