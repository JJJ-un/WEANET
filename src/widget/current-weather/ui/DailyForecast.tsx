import * as S from "@/shared/ui/card";
import WeatherIcon from "@/shared/assets/icons/weather.svg?react";

const DailyForecast = () => {
  // wiget은 피그마 ui 컴포넌트와 제일 가깝나?
  return (
    <S.Card className="w-[43px] h-[73px] flex-col items-center justify-center">
      <S.CardContent className="flex flex-col items-center justify-center gap-2">
        <S.CardTitle className="text-xs">지금</S.CardTitle>
        <div className=" flex flex-col items-center justify-center w-[20px] h-[20px]">
          <WeatherIcon />
        </div>
        <S.CardDescription className="text-xs">24°C</S.CardDescription>
      </S.CardContent>
    </S.Card>
  );
};

export default DailyForecast;
