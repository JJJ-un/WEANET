package com.weanet.server.api.weather.service;

import org.springframework.stereotype.Component;

@Component
public class WeatherAdviceGenerator {

    public String generateAdvice(String weather, double temp, int pop) {
        String weatherLower = weather.toLowerCase();
        if (weatherLower.contains("snow")) return "눈이 내리고 있어요. 길이 미끄러울 수 있으니 주의하세요! ❄️";
        if (weatherLower.contains("thunderstorm")) return "천둥번개를 동반한 비가 내려요. 가급적 외출을 삼가세요! ⚡";
        if (pop >= 50 || weatherLower.contains("rain")) return "비 소식이 있어요. 우산 꼭 챙기세요! ☂️";
        if (pop >= 20) return "강수 확률이 있어요. 혹시 모르니 작은 우산을 챙겨보세요. ☁️";

        if (temp < 4) return "날씨가 매우 추워요! 패딩이나 두꺼운 코트를 추천해요. ❄️🧥";
        if (temp < 12) return "쌀쌀한 날씨예요. 코트나 트렌치 코트를 입으세요. 🧥";
        if (temp < 23) return "긴팔 티셔츠나 얇은 가디건이 적당해요. 👔";
        return "날씨가 더워요! 시원한 옷차림과 수분 섭취 잊지 마세요. ☀️";
    }

    public String generateEmptyAdvice() {
        return "현재 기상 정보를 가져올 수 없습니다. 이동 시 주의하세요.";
    }
}
