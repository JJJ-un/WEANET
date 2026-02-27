package com.weanet.server.api.route.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoiResponse {
    private String name;
    private String address;
    private double lat;
    private double lng;
}
