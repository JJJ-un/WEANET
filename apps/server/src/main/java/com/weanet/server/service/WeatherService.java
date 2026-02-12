package com.weanet.server.service;

import com.weanet.server.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    /**
     * 좌표(위도, 경도)를 기반으로 기상청 날씨 정보를 조회합니다.
     */
    public WeatherResponse getWeatherByCoordinates(double lat, double lng) {
        if (isMockMode()) {
            return getMockWeather(15.0 + (lat % 5), 0.1 + (lng % 0.5));
        }

        // 1. 위경도 -> 기상청 격자 좌표(NX, NY) 변환
        LatLonToGrid.LatLon grid = LatLonToGrid.convert(lat, lng);
        
        // 2. 기상청 API 호출을 위한 기준 날짜/시간 설정 (단기예보 기준)
        String[] baseDateTime = getBaseDateTime();
        String baseDate = baseDateTime[0];
        String baseTime = baseDateTime[1];

        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("serviceKey", apiKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 1000)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", grid.nx)
                .queryParam("ny", grid.ny)
                .toUriString();

        return fetchWeatherData(url);
    }

    private boolean isMockMode() {
        return "YOUR_OPENWEATHERMAP_API_KEY".equals(apiKey) || "YOUR_KMA_API_KEY".equals(apiKey) || apiKey.isEmpty();
    }

    private WeatherResponse fetchWeatherData(String url) {
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.get("response") instanceof Map<?, ?> resMap) {
                Map<?, ?> body = (Map<?, ?>) resMap.get("body");
                if (body != null && body.get("items") instanceof Map<?, ?> itemsMap) {
                    List<Map<String, Object>> itemList = (List<Map<String, Object>>) itemsMap.get("item");
                    
                    return parseKmaData(itemList);
                }
            }
        } catch (Exception e) {
            System.err.println("Critical error fetching KMA weather data: " + e.getMessage());
        }
        return getMockWeather(12.0, 0.3);
    }

    /**
     * 기상청 응답 리스트에서 필요한 항목(TMP, POP, SKY 등)을 추출합니다.
     */
    private WeatherResponse parseKmaData(List<Map<String, Object>> itemList) {
        double currentTemp = 0.0;
        double pop = 0.0;
        String skyStatus = "1";
        String ptyStatus = "0";

        for (Map<String, Object> item : itemList) {
            String category = (String) item.get("category");
            String fcstValue = (String) item.get("fcstValue");

            switch (category) {
                case "TMP" -> currentTemp = Double.parseDouble(fcstValue);
                case "POP" -> pop = Double.parseDouble(fcstValue) / 100.0;
                case "SKY" -> skyStatus = fcstValue;
                case "PTY" -> ptyStatus = fcstValue;
            }
        }

        String weatherDesc = interpretSky(skyStatus, ptyStatus);
        return WeatherResponse.builder()
                .weather(weatherDesc)
                .currentTemp(currentTemp)
                .maxTemp(currentTemp + 5)
                .minTemp(currentTemp - 5)
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

    private String[] getBaseDateTime() {
        LocalDateTime now = LocalDateTime.now();
        int[] hours = {2, 5, 8, 11, 14, 17, 20, 23};
        int currentHour = now.getHour();
        int currentMinute = now.getMinute();

        if (currentMinute < 15) {
            now = now.minusHours(1);
            currentHour = now.getHour();
        }

        int targetHour = 2;
        for (int h : hours) {
            if (currentHour >= h) targetHour = h;
            else break;
        }

        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = String.format("%02d00", targetHour);
        return new String[]{baseDate, baseTime};
    }

    // 위경도 -> 격자 변환 헬퍼 클래스
    private static class LatLonToGrid {
        public static class LatLon {
            public int nx, ny;
        }

        public static LatLon convert(double lat, double lon) {
            double RE = 6371.00877;
            double GRID = 5.0;
            double SLAT1 = 30.0;
            double SLAT2 = 60.0;
            double OLON = 126.0;
            double OLAT = 38.0;
            double XO = 43;
            double YO = 136;

            double DEGRAD = Math.PI / 180.0;
            double re = RE / GRID;
            double slat1 = SLAT1 * DEGRAD;
            double slat2 = SLAT2 * DEGRAD;
            double olon = OLON * DEGRAD;
            double olat = OLAT * DEGRAD;

            double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
            sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
            double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
            sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
            double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
            ro = re * sf / Math.pow(ro, sn);

            LatLon rs = new LatLon();
            double ra = Math.tan(Math.PI * 0.25 + (lat) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = lon * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;
            rs.nx = (int) Math.floor(ra * Math.sin(theta) + XO + 0.5);
            rs.ny = (int) Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
            return rs;
        }
    }

    private String generateAdvice(String weather, double temp, double pop) {
        String weatherLower = weather.toLowerCase();
        
        if (weatherLower.contains("snow")) {
            return "눈이 내리고 있어요. 길이 미끄러울 수 있으니 주의하세요! ❄️";
        }
        if (weatherLower.contains("thunderstorm")) {
            return "천둥번개를 동반한 비가 내려요. 가급적 외출을 삼가세요! ⚡";
        }
        if (pop >= 0.5 || weatherLower.contains("rain") || weatherLower.contains("drizzle")) {
            return "비 소식이 있어요. 우산 꼭 챙기세요! ☂️";
        }
        if (pop >= 0.2) {
            return "강수 확률이 있어요. 혹시 모르니 작은 우산을 챙겨보세요. ☁️";
        }
        if (weatherLower.contains("mist") || weatherLower.contains("fog") || weatherLower.contains("haze")) {
            return "안개가 끼어 시야가 흐려요. 교통안전에 유의하세요! 🌫️";
        }

        if (temp < 4) return "날씨가 매우 추워요! 패딩이나 두꺼운 코트, 목도리를 추천해요. ❄️🧥";
        if (temp < 9) return "쌀쌀한 날씨예요. 코트나 가죽 자켓을 입는 게 좋겠어요. 🧥";
        if (temp < 12) return "자켓이나 트렌치 코트, 니트를 입기에 적당한 날씨예요. 🧣";
        if (temp < 17) return "얇은 가디건이나 맨투맨, 후드티를 추천해요. 👕";
        if (temp < 23) return "긴팔 티셔츠나 얇은 셔츠, 면바지가 적당해요. 👔";
        if (temp < 28) return "반팔 소매나 얇은 셔츠 등 가벼운 옷차림을 추천해요. 👕";
        
        return "날씨가 무척 더워요! 시원한 민소매나 반바지, 수분 섭취 잊지 마세요. ☀️🍹";
    }

    private WeatherResponse getMockWeather(double temp, double pop) {
        return WeatherResponse.builder()
                .weather("Cloudy (Mock)")
                .currentTemp(temp)
                .maxTemp(temp + 5)
                .minTemp(temp - 5)
                .precipitationProbability(pop)
                .advice(generateAdvice("Cloudy", temp, pop))
                .build();
    }
}
