import * as S from '@/shared/ui/Card';
import { WeatherIcon } from '@/entities/weather/ui/WeatherIcon';

const DailyForecast = () => {
  return (
    <S.Card className="flex flex-col items-center min-w-[4rem] py-4 gap-3 bg-transparent border-none shadow-none">
      {/* 시간 */}
      <S.CardTitle className="text-sm font-medium text-foreground">지금</S.CardTitle>
      {/* 아이콘 */}
      <div className="flex justify-center">
        <WeatherIcon status="Clear" className="w-8 h-8" />
      </div>
      {/* 기온 */}
      <span className="text-base font-semibold">24°</span>
    </S.Card>
  );
};

export default DailyForecast;
