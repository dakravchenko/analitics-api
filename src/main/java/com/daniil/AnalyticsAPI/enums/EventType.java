package com.daniil.AnalyticsAPI.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum EventType {
    PAGE_VIEW("PAGE_VIEW"), 
    BUTTON_CLICK("BUTTON_CLICK"), 
    FORM_SUBMIT("FORM_SUBMIT"), 
    FORM_ABANDON("FORM_ABANDON"), 
    ERROR("ERROR");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EventType fromValue(String value) {
        for (EventType type : EventType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid EventType: " + value);
    }
}