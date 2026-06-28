package com.daniil.AnalyticsAPI.events;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.daniil.AnalyticsAPI.enums.EventSource;
import com.daniil.AnalyticsAPI.enums.EventType;
import com.daniil.AnalyticsAPI.events.dto.response.EventReportResponse;
import com.daniil.AnalyticsAPI.events.dto.response.EventResponse;
import com.daniil.AnalyticsAPI.events.exception.RecordNotFoundException;
import com.daniil.AnalyticsAPI.events.models.EventModel;

public class EventsServiceTest {
    private EventsService eventsService;
    private UUID sessionId1;
    private UUID sessionId2;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        eventsService = new EventsService(new ArrayList<>());
        sessionId1 = UUID.randomUUID();
        sessionId2 = UUID.randomUUID();
    }

    @Test
    void testCreateEvent() {
        EventModel event = eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);

        assertNotNull(event);
        assertNotNull(event.getTraceId());
        assertEquals(EventType.PAGE_VIEW, event.getEventType());
        assertEquals(EventSource.WEB, event.getEventSource());
        assertEquals(sessionId1, event.getSessionId());
        assertEquals(1, eventsService.getEvents().size());
    }

    @Test
    void testCreateMultipleEvents() {
        eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);
        eventsService.createEvent(EventType.BUTTON_CLICK, EventSource.MOBILE, sessionId1);
        eventsService.createEvent(EventType.FORM_SUBMIT, EventSource.API, sessionId2);

        assertEquals(3, eventsService.getEvents().size());
    }

    @Test
    void testFindEventById() {
        EventModel createdEvent = eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);
        EventModel foundEvent = eventsService.findEventById(createdEvent.getTraceId());

        assertEquals(createdEvent.getTraceId(), foundEvent.getTraceId());
        assertEquals(EventType.PAGE_VIEW, foundEvent.getEventType());
    }

    @Test
    void testFindEventByIdNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(RecordNotFoundException.class, () -> {
            eventsService.findEventById(nonExistentId);
        });
    }

    @Test
    void testToResponse() {
        EventModel event = eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);
        EventResponse response = eventsService.toResponse(event);

        assertEquals(event.getTraceId(), response.traceId());
        assertEquals(event.getTimestamp(), response.timestamp());
        assertEquals(EventType.PAGE_VIEW, response.eventType());
        assertEquals(EventSource.WEB, response.eventSource());
    }

    @Test
    void testUpdateEvent() {
        EventModel createdEvent = eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);
        UUID eventId = createdEvent.getTraceId();

        EventModel updatedEvent = eventsService.updateEvent(eventId, EventType.BUTTON_CLICK, EventSource.MOBILE);

        assertEquals(eventId, updatedEvent.getTraceId());
        assertEquals(EventType.BUTTON_CLICK, updatedEvent.getEventType());
        assertEquals(EventSource.MOBILE, updatedEvent.getEventSource());
    }

    @Test
    void testUpdateEventNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(RecordNotFoundException.class, () -> {
            eventsService.updateEvent(nonExistentId, EventType.PAGE_VIEW, EventSource.WEB);
        });
    }

    @Test
    void testRemoveEvent() {
        EventModel event = eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);
        UUID eventId = event.getTraceId();

        assertEquals(1, eventsService.getEvents().size());

        eventsService.removeEvent(eventId);

        assertEquals(0, eventsService.getEvents().size());
    }

    @Test
    void testRemoveEventNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(RecordNotFoundException.class, () -> {
            eventsService.removeEvent(nonExistentId);
        });
    }

    @Test
    void testFilterEventsByType() {
        eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);
        eventsService.createEvent(EventType.PAGE_VIEW, EventSource.MOBILE, sessionId1);
        eventsService.createEvent(EventType.BUTTON_CLICK, EventSource.WEB, sessionId2);

        List<EventModel> filtered = eventsService.filterEvents(EventType.PAGE_VIEW, null, null, null);

        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(e -> e.getEventType() == EventType.PAGE_VIEW));
    }

    @Test
    void testFilterEventsBySource() {
        eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);
        eventsService.createEvent(EventType.BUTTON_CLICK, EventSource.WEB, sessionId1);
        eventsService.createEvent(EventType.FORM_SUBMIT, EventSource.MOBILE, sessionId2);

        List<EventModel> filtered = eventsService.filterEvents(null, EventSource.WEB, null, null);

        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(e -> e.getEventSource() == EventSource.WEB));
    }

    @Test
    void testFilterEventsByTypeAndSource() {
        eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);
        eventsService.createEvent(EventType.PAGE_VIEW, EventSource.MOBILE, sessionId1);
        eventsService.createEvent(EventType.BUTTON_CLICK, EventSource.WEB, sessionId2);

        List<EventModel> filtered = eventsService.filterEvents(EventType.PAGE_VIEW, EventSource.WEB, null, null);

        assertEquals(1, filtered.size());
        assertEquals(EventType.PAGE_VIEW, filtered.get(0).getEventType());
        assertEquals(EventSource.WEB, filtered.get(0).getEventSource());
    }

    @Test
    void testFilterEventsByTimeRange() {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(10);
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(10);

        eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);
        eventsService.createEvent(EventType.BUTTON_CLICK, EventSource.MOBILE, sessionId1);

        List<EventModel> filtered = eventsService.filterEvents(null, null, startTime, endTime);

        assertEquals(2, filtered.size());
    }

    @Test
    void testFilterEventsNoFilters() {
        eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);
        eventsService.createEvent(EventType.BUTTON_CLICK, EventSource.MOBILE, sessionId2);

        List<EventModel> filtered = eventsService.filterEvents(null, null, null, null);

        assertEquals(2, filtered.size());
    }

    @Test
    void testGenerateReport() {
        eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);
        eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);
        eventsService.createEvent(EventType.BUTTON_CLICK, EventSource.MOBILE, sessionId1);
        eventsService.createEvent(EventType.FORM_SUBMIT, EventSource.API, sessionId2);

        EventReportResponse report = eventsService.generateReport();

        assertEquals(4, report.totalEntries());
        assertEquals(2, report.totalUniqueSessions());
    }

    @Test
    void testGenerateReportEventTypeSummary() {
        eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);
        eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);
        eventsService.createEvent(EventType.BUTTON_CLICK, EventSource.MOBILE, sessionId2);

        EventReportResponse report = eventsService.generateReport();
        Map<String, Integer> typeSummary = report.eventTypeSummary();

        assertEquals(2, typeSummary.get("PAGE_VIEW"));
        assertEquals(1, typeSummary.get("BUTTON_CLICK"));
    }

    @Test
    void testGenerateReportEventSourceSummary() {
        eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);
        eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);
        eventsService.createEvent(EventType.BUTTON_CLICK, EventSource.MOBILE, sessionId2);

        EventReportResponse report = eventsService.generateReport();
        Map<String, Integer> sourceSummary = report.eventSourceSummary();

        assertEquals(2, sourceSummary.get("WEB"));
        assertEquals(1, sourceSummary.get("MOBILE"));
    }

    @Test
    void testGenerateReportEmpty() {
        EventReportResponse report = eventsService.generateReport();

        assertEquals(0, report.totalEntries());
        assertEquals(0, report.totalUniqueSessions());
        assertEquals(0, report.eventTypeSummary().size());
        assertEquals(0, report.eventSourceSummary().size());
    }

    @Test
    void testSetEvents() {
        List<EventModel> newEvents = new ArrayList<>();
        EventModel event1 = new EventModel(UUID.randomUUID(), LocalDateTime.now(), EventType.PAGE_VIEW,
                EventSource.WEB, sessionId1);
        newEvents.add(event1);

        eventsService.setEvents(newEvents);

        assertEquals(1, eventsService.getEvents().size());
        assertEquals(event1, eventsService.getEvents().get(0));
    }

    @Test
    void testGetEvents() {
        assertTrue(eventsService.getEvents().isEmpty());

        eventsService.createEvent(EventType.PAGE_VIEW, EventSource.WEB, sessionId1);
        eventsService.createEvent(EventType.BUTTON_CLICK, EventSource.MOBILE, sessionId2);

        List<EventModel> events = eventsService.getEvents();

        assertEquals(2, events.size());
    }
}
