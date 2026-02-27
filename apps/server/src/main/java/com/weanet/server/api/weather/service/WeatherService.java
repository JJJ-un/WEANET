package com.weanet.server.api.weather.service;

import com.weanet.server.api.external.service.ExternalMapService;
import com.weanet.server.api.external.util.KmaCoordinateConverter;
import com.weanet.server.api.weather.dto.HourlyWeatherResponse;
import com.weanet.server.api.weather.dto.KmaWeatherApiResponse;
import com.weanet.server.api.weather.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
     * 도시 이름을 기반으로 날씨 정보를 조회합니다. (시간대별 예보 포함)
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
     * 좌표(위도, 경도)를 기반으로 날씨 정보를 조회합니다. (시간대별 예보 포함)
     */
    public WeatherResponse getWeatherByCoordinates(double lat, double lng) {
        String url = buildKmaUrl(lat, lng);
        return fetchAndParseWeather(url, true);
    }

    /**
     * 좌표를 기반으로 현재 날씨 정보만 조회합니다. (시간대별 예보 제외 - 경로 보강용)
     */
    public WeatherResponse getCurrentWeatherByCoordinates(double lat, double lng) {
        String url = buildKmaUrl(lat, lng);
        return fetchAndParseWeather(url, false);
    }

    private String buildKmaUrl(double lat, double lng) {
        KmaCoordinateConverter.Grid grid = coordinateConverter.convertToGrid(lat, lng);
        String[] baseDateTime = calculateBaseDateTime();

        return UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("serviceKey", apiKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 1000)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDateTime[0])
                .queryParam("base_time", baseDateTime[1])
                .queryParam("nx", grid.nx)
                .queryParam("ny", grid.ny)
                .build(true).toUriString();
    }

    private WeatherResponse fetchAndParseWeather(String url, boolean includeHourly) {
        try {
            log.info("기상청 API 호출 URL: {}", url);
            KmaWeatherApiResponse response = restTemplate.getForObject(url, KmaWeatherApiResponse.class);
            
            if (response != null && response.getResponse() != null) {
                if ("00".equals(response.getResponse().getHeader().getResultCode())) {
                    List<KmaWeatherApiResponse.Item> items = response.getResponse().getBody().getItems().getItem();
                    return buildWeatherResponse(items, includeHourly);
                } else {
                    log.warn("기상청 API 응답 에러: {} - {}", 
                        response.getResponse().getHeader().getResultCode(), 
                        response.getResponse().getHeader().getResultMsg());
                }
            }
        } catch (Exception e) {
            log.error("기상청 API 호출 중 오류 발생: {}", e.getMessage());
        }
        return buildEmptyWeatherResponse();
    }

    private WeatherResponse buildWeatherResponse(List<KmaWeatherApiResponse.Item> items, boolean includeHourly) {
        Map<String, List<KmaWeatherApiResponse.Item>> groupedByTime = items.stream()
                .collect(Collectors.groupingBy(item -> item.getFcstDate() + item.getFcstTime(), 
                        TreeMap::new, Collectors.toList()));

        List<HourlyWeatherResponse> hourlyForecast = new ArrayList<>();
        double currentTemp = Double.NaN;
        double currentPop = 0.0;
        double minTemp = Double.NaN;
        double maxTemp = Double.NaN;
        String currentSky = "1";
        String currentPty = "0";
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        for (Map.Entry<String, List<KmaWeatherApiResponse.Item>> entry : groupedByTime.entrySet()) {
            List<KmaWeatherApiResponse.Item> timeItems = entry.getValue();
            String date = timeItems.get(0).getFcstDate();
            String time = timeItems.get(0).getFcstTime();

            double temp = 0.0;
            double pop = 0.0;
            String sky = "1";
            String pty = "0";

            for (KmaWeatherApiResponse.Item item : timeItems) {
                String value = item.getFcstValue();
                switch (item.getCategory()) {
                    case "TMP" -> temp = Double.parseDouble(value);
                    case "POP" -> pop = Double.parseDouble(value) / 100.0;
                    case "SKY" -> sky = value;
                    case "PTY" -> pty = value;
                    case "TMN" -> { if (date.equals(today)) minTemp = Double.parseDouble(value); }
                    case "TMX" -> { if (date.equals(today)) maxTemp = Double.parseDouble(value); }
                }
            }

            if (Double.isNaN(currentTemp)) {
                currentTemp = temp;
                currentPop = pop;
                currentSky = sky;
                currentPty = pty;
            }

            if (includeHourly) {
                hourlyForecast.add(HourlyWeatherResponse.builder()
                        .fcstDate(date)
                        .fcstTime(time)
                        .temp(temp)
                        .weather(interpretSky(sky, pty))
                        .precipitationProbability(pop)
                        .build());
            }
        }

        if (Double.isNaN(minTemp)) minTemp = currentTemp - 2;
        if (Double.isNaN(maxTemp)) maxTemp = currentTemp + 5;

        String weatherDesc = interpretSky(currentSky, currentPty);
        WeatherResponse.WeatherResponseBuilder builder = WeatherResponse.builder()
                .weather(weatherDesc)
                .currentTemp(currentTemp)
                .maxTemp(maxTemp)
                .minTemp(minTemp)
                .precipitationProbability(currentPop)
                .advice(generateAdvice(weatherDesc, currentTemp, currentPop));

        if (includeHourly && !hourlyForecast.isEmpty()) {
            builder.hourlyForecast(hourlyForecast);
        }

        return builder.build();
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
