package com.weanet.server.service;

import com.weanet.server.dto.KmaWeatherApiResponse;
import com.weanet.server.dto.WeatherResponse;
import com.weanet.server.util.KmaCoordinateConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RestTemplate restTemplate;
    private final KmaCoordinateConverter coordinateConverter;
    private final ExternalMapService externalMapService;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    /**
     * 도시 이름을 기반으로 날씨 정보를 조회합니다.
     */
    public WeatherResponse getWeather(String city) {
        double[] coords = getCoordinates(city);
        return getWeatherByCoordinates(coords[0], coords[1]);
    }

    /**
     * 명칭 기반으로 좌표를 검색합니다.
     */
    public double[] getCoordinates(String keyword) {
        return externalMapService.getCoordinates(keyword);
    }

    /**
     * 좌표(위도, 경도)를 기반으로 기상청 날씨 정보를 조회합니다.
     */
    public WeatherResponse getWeatherByCoordinates(double lat, double lng) {
        KmaCoordinateConverter.Grid grid = coordinateConverter.convertToGrid(lat, lng);
        String[] baseDateTime = calculateBaseDateTime();

        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("serviceKey", apiKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 1000)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDateTime[0])
                .queryParam("base_time", baseDateTime[1])
                .queryParam("nx", grid.nx)
                .queryParam("ny", grid.ny)
                .build(true).toUriString(); // 서비스키 인코딩 방지를 위해 build(true) 사용

        return fetchAndParseWeather(url);
    }

    private WeatherResponse fetchAndParseWeather(String url) {
        try {
            KmaWeatherApiResponse response = restTemplate.getForObject(url, KmaWeatherApiResponse.class);
            
            if (response != null && response.getResponse().getBody() != null) {
                List<KmaWeatherApiResponse.Item> items = response.getResponse().getBody().getItems().getItem();
                return buildWeatherResponse(items);
            }
        } catch (Exception e) {
            log.error("기상청 API 호출 중 오류 발생: {}", e.getMessage());
        }
        
        return buildEmptyWeatherResponse();
    }

    private WeatherResponse buildWeatherResponse(List<KmaWeatherApiResponse.Item> items) {
        double currentTemp = Double.NaN;
        double pop = Double.NaN;
        double minTemp = Double.NaN;
        double maxTemp = Double.NaN;
        String skyStatus = "1";
        String ptyStatus = "0";
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        for (KmaWeatherApiResponse.Item item : items) {
            String category = item.getCategory();
            String value = item.getFcstValue();

            switch (category) {
                case "TMP" -> {
                    // 첫 번째로 만나는 TMP(현재 시각과 가장 가까운 예보)를 선택
                    if (Double.isNaN(currentTemp)) currentTemp = Double.parseDouble(value);
                }
                case "POP" -> {
                    if (Double.isNaN(pop)) pop = Double.parseDouble(value) / 100.0;
                }
                case "SKY" -> {
                    if (skyStatus.equals("1")) skyStatus = value;
                }
                case "PTY" -> {
                    if (ptyStatus.equals("0")) ptyStatus = value;
                }
                case "TMN" -> {
                    if (today.equals(item.getFcstDate())) minTemp = Double.parseDouble(value);
                }
                case "TMX" -> {
                    if (today.equals(item.getFcstDate())) maxTemp = Double.parseDouble(value);
                }
            }
        }

        // 데이터 보완 (기본값 설정)
        if (Double.isNaN(currentTemp)) currentTemp = 0.0;
        if (Double.isNaN(pop)) pop = 0.0;
        if (Double.isNaN(minTemp)) minTemp = currentTemp - 2;
        if (Double.isNaN(maxTemp)) maxTemp = currentTemp + 5;

        String weatherDesc = interpretSky(skyStatus, ptyStatus);
        return WeatherResponse.builder()
                .weather(weatherDesc)
                .currentTemp(currentTemp)
                .maxTemp(maxTemp)
                .minTemp(minTemp)
                .precipitationProbability(pop)
                .advice(generateAdvice(weatherDesc, currentTemp, pop))
                .build();
    }

    private String interpretSky(String sky, String pty) {
        if (!"0".equals(pty)) {
            return switch (pty) {
                case "1" -> "Rain";
                case "2" -> "Rain/Snow";
                case "3" -> "Snow";
                case "4" -> "Shower";
                default -> "Rain";
            };
        }
        return switch (sky) {
            case "1" -> "Clear";
            case "3" -> "Cloudy";
            case "4" -> "Overcast";
            default -> "Clear";
        };
    }

    private String[] calculateBaseDateTime() {
        LocalDateTime now = LocalDateTime.now();
        int[] announcementHours = {2, 5, 8, 11, 14, 17, 20, 23};
        
        // 기상청 업데이트 지연 고려 (15분)
        if (now.getMinute() < 15) {
            now = now.minusHours(1);
        }

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

    private String generateAdvice(String weather, double temp, double pop) {
        String weatherLower = weather.toLowerCase();
        if (weatherLower.contains("snow")) return "눈이 내리고 있어요. 길이 미끄러울 수 있으니 주의하세요! ❄️";
        if (weatherLower.contains("thunderstorm")) return "천둥번개를 동반한 비가 내려요. 가급적 외출을 삼가세요! ⚡";
        if (pop >= 0.5 || weatherLower.contains("rain")) return "비 소식이 있어요. 우산 꼭 챙기세요! ☂️";
        if (pop >= 0.2) return "강수 확률이 있어요. 혹시 모르니 작은 우산을 챙겨보세요. ☁️";

        if (temp < 4) return "날씨가 매우 추워요! 패딩이나 두꺼운 코트를 추천해요. ❄️🧥";
        if (temp < 12) return "쌀쌀한 날씨예요. 코트나 트렌치 코트를 입으세요. 🧥";
        if (temp < 23) return "긴팔 티셔츠나 얇은 가디건이 적당해요. 👔";
        return "날씨가 더워요! 시원한 옷차림과 수분 섭취 잊지 마세요. ☀️";
    }

    private WeatherResponse buildEmptyWeatherResponse() {
        return WeatherResponse.builder()
                .weather("Unknown")
                .advice("현재 기상 정보를 가져올 수 없습니다. 이동 시 주의하세요.")
                .build();
    }
}
