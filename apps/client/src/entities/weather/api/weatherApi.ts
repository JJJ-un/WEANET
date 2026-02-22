import { apiClient } from '@/shared/api/base';

/**
 * 서버 응답 데이터 인터페이스
 */
export interface WeatherResponse {
    weather: string; // "Clear", "Rain", "Snow", "Cloudy", "Unknown" 등
    currentTemp: number;
    maxTemp: number;
    minTemp: number;
    precipitationProbability: number;
    advice: string;
}

/**
 * 특정 도시의 날씨 정보를 가져오는 API
 * @param city 도시 이름 (ex: 'Seoul', 'Incheon')
 */
export const fetchWeather = async (city: string): Promise<WeatherResponse> => {
    const { data } = await apiClient.get<WeatherResponse>(`/weather`, {
        params: { city },
    });
    return data;
};
