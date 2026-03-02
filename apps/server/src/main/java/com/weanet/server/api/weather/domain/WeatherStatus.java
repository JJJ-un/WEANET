package com.weanet.server.api.weather.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WeatherStatus {
    CLEAR("Clear"),
    CLOUDY("Cloudy"),
    OVERCAST("Overcast"),
    RAIN("Rain"),
    RAIN_SNOW("Rain/Snow"),
    SNOW("Snow"),
    SHOWER("Shower");

    private final String description;

    public static String interpret(String sky, String pty) {
        if (!"0".equals(pty)) {
            return switch (pty) {
                case "1" -> RAIN.description;
                case "2" -> RAIN_SNOW.description;
                case "3" -> SNOW.description;
                case "4" -> SHOWER.description;
                default -> RAIN.description;
            };
        }
        return switch (sky) {
            case "1" -> CLEAR.description;
            case "3" -> CLOUDY.description;
            case "4" -> OVERCAST.description;
            default -> CLEAR.description;
        };
    }
}
