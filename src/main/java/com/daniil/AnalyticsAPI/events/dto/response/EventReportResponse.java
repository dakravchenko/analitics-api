package com.daniil.AnalyticsAPI.events.dto.response;

import java.util.Map;

public record EventReportResponse(
    int totalEntries,
    int totalUniqueSessions,
    Map<String, Integer> eventTypeSummary,
    Map<String, Integer> eventSourceSummary
) {}