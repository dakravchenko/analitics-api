package com.daniil.AnalyticsAPI.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum EventSource {
    WEB("WEB"), 
    MOBILE("MOBILE"), 
    API("API");

    private final String value;

    EventSource(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EventSource fromValue(String value) {
        for (EventSource source : EventSource.values()) {
            if (source.value.equalsIgnoreCase(value)) {
                return source;
            }
        }
        throw new IllegalArgumentException("Invalid EventSource: " + value);
    }
}