package com.weanet.server.api.route.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubwayRealtimeResponse {
    private String lineName;
    private String arrivalMessage;
    private String status;
}
