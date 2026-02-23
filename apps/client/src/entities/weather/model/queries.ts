import { useQuery } from '@tanstack/react-query';
import { fetchWeather } from '../api/weatherApi';
import { type WeatherData, type WeatherStatus } from './types';

/**
 * 서버에서 오는 영어 상태값을 우리 앱의 WeatherStatus 타입으로 매핑
 */
const mapStatus = (status: string): WeatherStatus => {
    const validStatuses: WeatherStatus[] = ['Clear', 'Rain', 'Snow', 'Cloudy', 'Overcast'];
    if (validStatuses.includes(status as WeatherStatus)) {
        return status as WeatherStatus;
    }
    return 'Cloudy'; // 기본값
};

/**
 * "0600" 형태의 시간을 "6시" 형태로 변환
 */
const formatTime = (timeStr: string) => {
    const hour = parseInt(timeStr.substring(0, 2), 10);
    return `${hour}시`;
};

/**
 * 날씨 데이터를 불러오는 TanStack Query 훅
 */
export const useWeather = (city: string) => {
    return useQuery({
        queryKey: ['weather', city],
        queryFn: async () => {
            const data = await fetchWeather(city);

            // 앱 내부 인터페이스(WeatherData)로 변환 (Mapping)
            const mappedData: WeatherData = {
                status: mapStatus(data.weather),
                currentTemp: Math.round(data.currentTemp),
                maxTemp: Math.round(data.maxTemp),
                minTemp: Math.round(data.minTemp),
                precipitation: Math.round(data.precipitationProbability * 100), // 0.3 -> 30%
                advice: data.advice,
                hourlyForecast: data.hourlyForecast.slice(0, 24).map((item) => ({
                    time: formatTime(item.fcstTime),
                    status: mapStatus(item.weather),
                    temp: Math.round(item.temp),
                })),
            };

            return mappedData;
        },
        enabled: !!city,
        staleTime: 1000 * 60 * 5, // 5분 동안은 신선한 데이터로 간주
    });
};
