package com.weanet.server.api.route.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransportType {
    WALK("도보"),
    BUS("버스"),
    SUBWAY("지하철");

    private final String description;
}
