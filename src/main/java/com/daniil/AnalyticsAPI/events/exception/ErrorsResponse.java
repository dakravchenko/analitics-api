package com.daniil.AnalyticsAPI.events.exception;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorsResponse {
    private List<String> errors;
}