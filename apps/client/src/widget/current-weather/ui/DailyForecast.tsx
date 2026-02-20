import * as S from '@/shared/ui/Card';
import { WeatherIcon } from '@/entities/weather/ui/WeatherIcon';
import { type HourlyForecast } from '@/entities/weather/model/types';

const DailyForecast = ({ time, status, temp }: HourlyForecast) => {
  return (
    <S.Card className="flex flex-col items-center min-w-[4.8rem] py-4 px-2 gap-3 bg-white border border-border shadow-sm">
      {/* 시간 */}
      <S.CardTitle className="text-sm font-medium text-foreground">{time}</S.CardTitle>
      {/* 아이콘 */}
      <div className="flex justify-center">
        <WeatherIcon status={status} className="w-8 h-8" />
      </div>
      {/* 기온 */}
      <span className="text-base font-semibold text-foreground">{temp}°</span>
    </S.Card>
  );
};

export default DailyForecast;
