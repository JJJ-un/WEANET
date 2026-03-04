package com.weanet.server.api.weather.service;

import com.weanet.server.api.external.util.KmaCoordinateConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class KmaUriBuilder {

    private final KmaCoordinateConverter coordinateConverter;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    public String build(double lat, double lng, int numOfRows) {
        KmaCoordinateConverter.Grid grid = coordinateConverter.convertToGrid(lat, lng);
        String[] baseDateTime = calculateBaseDateTime();

        return UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("serviceKey", apiKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", numOfRows)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDateTime[0])
                .queryParam("base_time", baseDateTime[1])
                .queryParam("nx", grid.nx)
                .queryParam("ny", grid.ny)
                .build(true).toUriString();
    }

    private String[] calculateBaseDateTime() {
        ZonedDateTime now = ZonedDateTime.now(KST);
        int[] announcementHours = {2, 5, 8, 11, 14, 17, 20, 23};
        // API 갱신 지연(약 10~15분)을 고려하여 15분 전 시간을 기준으로 잡음
        if (now.getMinute() < 15) now = now.minusHours(1);

        int currentHour = now.getHour();
        int lastAnnouncement = 2;
        for (int hour : announcementHours) {
            if (currentHour >= hour) lastAnnouncement = hour;
            else break;
        }

        return new String[]{
            now.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
            String.format("%02d00", lastAnnouncement)
        };
    }
}
