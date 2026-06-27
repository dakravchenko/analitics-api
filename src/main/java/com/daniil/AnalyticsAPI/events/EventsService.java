package com.daniil.AnalyticsAPI.events;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.daniil.AnalyticsAPI.events.models.EventModel;
import com.daniil.AnalyticsAPI.enums.EventSource;
import com.daniil.AnalyticsAPI.enums.EventType;
import com.daniil.AnalyticsAPI.events.dto.response.EventResponse;
import com.daniil.AnalyticsAPI.events.exception.RecordNotFoundException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Service
@Getter
@Setter
@AllArgsConstructor
public class EventsService {
    private List<EventModel> events = new ArrayList<>();

    public EventResponse toResponse(EventModel event) {
        return new EventResponse(
                event.getTraceId(),
                event.getTimestamp(),
                event.getEventType(),
                event.getEventSource());
    }

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

    public EventModel createEvent(EventType eventType, EventSource eventSource, UUID sessionId) {
        EventModel event = new EventModel(UUID.randomUUID(), LocalDateTime.now(), eventType, eventSource, sessionId);

        events.add(event);
        return event;
    }

}