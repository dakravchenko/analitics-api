package com.daniil.AnalyticsAPI.events;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daniil.AnalyticsAPI.enums.EventSource;
import com.daniil.AnalyticsAPI.enums.EventType;
import com.daniil.AnalyticsAPI.events.dto.request.CreateEventRequest;
import com.daniil.AnalyticsAPI.events.dto.request.UpdateEventRequest;
import com.daniil.AnalyticsAPI.events.dto.response.EventReportResponse;
import com.daniil.AnalyticsAPI.events.dto.response.EventResponse;
import com.daniil.AnalyticsAPI.events.models.EventModel;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/events")
public class EventsController {
    private final EventsService eventsService;

    public EventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents(
            @RequestParam(required = false) EventType eventType,
            @RequestParam(required = false) EventSource eventSource,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime) {

        List<EventModel> filteredEvents = eventsService.filterEvents(eventType, eventSource, startTime, endTime);

        List<EventResponse> responses = filteredEvents.stream()
                .map(eventsService::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<EventResponse> addEvent(@Valid @RequestBody CreateEventRequest request) {
        EventModel newEvent = eventsService.createEvent(
                request.eventType(),
                request.eventSource(),
                request.sessionId());

        EventResponse convertedEvent = eventsService.toResponse(newEvent);

        URI location = URI.create("/api/v1/events/" + convertedEvent.traceId());

        return ResponseEntity.created(location).body(convertedEvent);
    }

    @GetMapping("/report")
    public ResponseEntity<EventReportResponse> getReport() {
        EventReportResponse report = eventsService.generateReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable UUID id) {
        EventModel event = eventsService.findEventById(id);
        EventResponse convertedEvent = eventsService.toResponse(event);

        return convertedEvent != null ? ResponseEntity.ok(convertedEvent) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEventById(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEventRequest request) {

        EventModel updatedEvent = eventsService.updateEvent(id, request.eventType(), request.eventSource());
        EventResponse convertedEvent = eventsService.toResponse(updatedEvent);

        return ResponseEntity.ok(convertedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        eventsService.removeEvent(id);
        return ResponseEntity.noContent().build();
    }

}
