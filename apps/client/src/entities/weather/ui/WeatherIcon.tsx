import DefaultWeatherIcon from '@/shared/assets/icons/weather.svg?react';
import { type WeatherStatus } from '../model/types';

interface WeatherIconProps {
  status: WeatherStatus;
  className?: string;
}

/**
 * 날씨 상태별 Tailwind 색상 클래스 매핑
 */
const STATUS_COLORS: Record<WeatherStatus, string> = {
  Clear: 'text-[var(--weather-clear)]',
  Rain: 'text-[var(--weather-rain)]',
  Snow: 'text-[var(--weather-snow)]',
  Cloudy: 'text-[var(--weather-cloudy)]',
};

/**
 * 날씨 상태에 따라 아이콘과 색상을 동적으로 변경하는 컴포넌트
 * @param status 'Clear' | 'Rain' | 'Snow' | 'Cloudy'
 * @param className Tailwind 클래스 등을 통한 스타일링 확장
 */
export const WeatherIcon = ({ status, className }: WeatherIconProps) => {
  const Icon = DefaultWeatherIcon;
  const statusColorClass = STATUS_COLORS[status];

  return (
    <div className={`${statusColorClass} ${className}`}>
      <Icon className="w-full h-full fill-current" />
    </div>
  );
};
