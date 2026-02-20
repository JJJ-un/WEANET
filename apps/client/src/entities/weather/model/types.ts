export type WeatherStatus = 'Clear' | 'Rain' | 'Snow' | 'Cloudy';

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
    label: '흐림',
    color: 'var(--weather-cloudy)',
  },
};
