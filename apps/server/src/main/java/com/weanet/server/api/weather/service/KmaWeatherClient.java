package com.weanet.server.api.weather.service;

import com.weanet.server.global.error.exception.BusinessException;
import com.weanet.server.global.error.exception.ErrorCode;
import com.weanet.server.api.weather.dto.response.KmaWeatherApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KmaWeatherClient {

    private final RestTemplate restTemplate;
    private final KmaUriBuilder uriBuilder;

    public List<KmaWeatherApiResponse.Item> fetchWeatherData(double lat, double lng, int numOfRows) {
        String url = uriBuilder.build(lat, lng, numOfRows);
        
        try {
            KmaWeatherApiResponse response = restTemplate.getForObject(url, KmaWeatherApiResponse.class);
            
            if (response != null && response.getResponse() != null && "00".equals(response.getResponse().getHeader().getResultCode())) {
                return response.getResponse().getBody().getItems().getItem();
            }
            
            log.error("기상청 API 응답 오류: {}", (response != null && response.getResponse() != null) ? 
                    response.getResponse().getHeader().getResultMsg() : "Empty Response");
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR);
            
        } catch (Exception e) {
            log.error("기상청 API 호출 중 예외 발생: {}", e.getMessage());
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }
}
