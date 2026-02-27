export type WeatherStatus = 'Clear' | 'Rain' | 'Snow' | 'Cloudy' | 'Overcast';

export interface WeatherConfig {
    label: string;
    color: string;
}

export interface WeatherData {
    status: WeatherStatus;
    currentTemp: number;
    maxTemp: number;
    minTemp: number;
    precipitation: number; // 강수 확률 (%)
    hourlyForecast: HourlyForecast[];
}

export interface HourlyForecast {
    time: string;
    status: WeatherStatus;
    temp: number;
}

export const WEATHER_CONFIG: Record<WeatherStatus, WeatherConfig> = {
    Clear: {
        label: '맑음',
        color: 'var(--weather-clear)',
    },
    Rain: {
        label: '비',
        color: 'var(--weather-rain)',
    },
    Snow: {
        label: '눈',
        color: 'var(--weather-snow)',
    },
    Cloudy: {
        label: '구름 많음',
        color: 'var(--weather-cloudy)',
    },
    Overcast: {
        label: '흐림',
        color: 'var(--weather-overcast, #94a3b8)',
    },
};
