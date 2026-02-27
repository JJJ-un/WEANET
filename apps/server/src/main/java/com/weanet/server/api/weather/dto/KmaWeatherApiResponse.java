package com.weanet.server.api.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 기상청 단기예보 API 응답 매핑용 DTO
 */
@Getter
@NoArgsConstructor
public class KmaWeatherApiResponse {

    @JsonProperty("response")
    private Response response;

    @Getter
    @NoArgsConstructor
    public static class Response {
        private Header header;
        private Body body;
    }

    @Getter
    @NoArgsConstructor
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Getter
    @NoArgsConstructor
    public static class Body {
        private Items items;
        private int numOfRows;
        private int pageNo;
        private int totalCount;
    }

    @Getter
    @NoArgsConstructor
    public static class Items {
        private List<Item> item;
    }

    @Getter
    @NoArgsConstructor
    public static class Item {
        private String baseDate;
        private String baseTime;
        private String category;
        private String fcstDate;
        private String fcstTime;
        private String fcstValue;
        private int nx;
        private int ny;
    }
}
