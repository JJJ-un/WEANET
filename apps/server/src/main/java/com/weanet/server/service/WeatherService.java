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
     * 도시 이름을 기반으로 날씨 정보를 조회합니다.
     */
    public WeatherResponse getWeather(String city) {
        // 주요 도시 좌표 매핑 (추후 DB 연동 권장)
        double lat = 37.5665; double lng = 126.9780; // 서울
        if ("Busan".equalsIgnoreCase(city)) { lat = 35.1796; lng = 129.0756; }
        else if ("Incheon".equalsIgnoreCase(city)) { lat = 37.4563; lng = 126.7052; }

        return getWeatherByCoordinates(lat, lng);
    }

    /**
     * 좌표(위도, 경도)를 기반으로 기상청 날씨 정보를 조회합니다.
     */
    public WeatherResponse getWeatherByCoordinates(double lat, double lng) {
        // 1. 위경도 -> 기상청 격자 좌표(NX, NY) 변환
        LatLonToGrid.LatLon grid = LatLonToGrid.convert(lat, lng);
        
        // 2. 기상청 API 호출을 위한 기준 날짜/시간 설정
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
            System.err.println("KMA API fetch error: " + e.getMessage());
        }
        
        // API 호출 실패 시 '정보 없음'을 나타내는 응답 반환
        return WeatherResponse.builder()
                .weather("Unknown")
                .advice("현재 기상 정보를 가져올 수 없습니다. 이동 시 주의하세요.")
                .build();
    }

    private WeatherResponse parseKmaData(List<Map<String, Object>> itemList) {
        double currentTemp = 0.0;
        double pop = 0.0;
        double minTemp = Double.NaN;
        double maxTemp = Double.NaN;
        String skyStatus = "1";
        String ptyStatus = "0";
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        for (Map<String, Object> item : itemList) {
            String category = (String) item.get("category");
            String fcstValue = (String) item.get("fcstValue");
            String fcstDate = (String) item.get("fcstDate");

            switch (category) {
                case "TMP" -> { if (currentTemp == 0.0) currentTemp = Double.parseDouble(fcstValue); }
                case "POP" -> { if (pop == 0.0) pop = Double.parseDouble(fcstValue) / 100.0; }
                case "SKY" -> { if (skyStatus.equals("1")) skyStatus = fcstValue; }
                case "PTY" -> { if (ptyStatus.equals("0")) ptyStatus = fcstValue; }
                case "TMN" -> { if (today.equals(fcstDate)) minTemp = Double.parseDouble(fcstValue); }
                case "TMX" -> { if (today.equals(fcstDate)) maxTemp = Double.parseDouble(fcstValue); }
            }
        }

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

    private String[] getBaseDateTime() {
        LocalDateTime now = LocalDateTime.now();
        int[] hours = {2, 5, 8, 11, 14, 17, 20, 23};
        int currentHour = now.getHour();
        int currentMinute = now.getMinute();

        if (currentMinute < 15) { now = now.minusHours(1); currentHour = now.getHour(); }

        int targetHour = 2;
        for (int h : hours) { if (currentHour >= h) targetHour = h; else break; }

        return new String[]{now.format(DateTimeFormatter.ofPattern("yyyyMMdd")), String.format("%02d00", targetHour)};
    }

    private String generateAdvice(String weather, double temp, double pop) {
        String weatherLower = weather.toLowerCase();
        if (weatherLower.contains("snow")) return "눈이 내리고 있어요. 길이 미끄러울 수 있으니 주의하세요! ❄️";
        if (weatherLower.contains("thunderstorm")) return "천둥번개를 동반한 비가 내려요. 가급적 외출을 삼가세요! ⚡";
        if (pop >= 0.5 || weatherLower.contains("rain")) return "비 소식이 있어요. 우산 꼭 챙기세요! ☂️";
        if (pop >= 0.2) return "강수 확률이 있어요. 혹시 모르니 작은 우산을 챙겨보세요. ☁️";
        if (weatherLower.contains("mist") || weatherLower.contains("fog")) return "안개가 끼어 시야가 흐려요. 교통안전에 유의하세요! 🌫️";

        if (temp < 4) return "날씨가 매우 추워요! 패딩이나 두꺼운 코트를 추천해요. ❄️🧥";
        if (temp < 12) return "쌀쌀한 날씨예요. 코트나 트렌치 코트를 입으세요. 🧥";
        if (temp < 23) return "긴팔 티셔츠나 얇은 가디건이 적당해요. 👔";
        return "날씨가 더워요! 시원한 옷차림과 수분 섭취 잊지 마세요. ☀️";
    }

    private static class LatLonToGrid {
        public static class LatLon { public int nx, ny; }
        public static LatLon convert(double lat, double lon) {
            double RE = 6371.00877; double GRID = 5.0; double SLAT1 = 30.0; double SLAT2 = 60.0;
            double OLON = 126.0; double OLAT = 38.0; double XO = 43; double YO = 136;
            double DEGRAD = Math.PI / 180.0; double re = RE / GRID;
            double slat1 = SLAT1 * DEGRAD; double slat2 = SLAT2 * DEGRAD;
            double olon = OLON * DEGRAD; double olat = OLAT * DEGRAD;
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
}
