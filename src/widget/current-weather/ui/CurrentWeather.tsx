import * as S from "@/shared/ui/card";
import WeatherIcon from "@/shared/assets/icons/weather.svg?react";

const CurrentWeather = () => {
  // wiget은 피그마 ui 컴포넌트와 제일 가깝나?
  return (
    <S.Card className="w-[100%] h-[91px] flex justify-center items-center gap-8">
      <S.CardContent className="flex justify-between items-center w-full">
        <div className="flex items-center gap-6">
          <WeatherIcon />
          <div className="flex flex-col gap-1">
            <S.CardDescription>강남구 현재 날씨</S.CardDescription>
            <S.CardTitle className="text-xl">맑음, 25°C</S.CardTitle>
          </div>
        </div>
        <div>
          <S.CardDescription>강수 10%</S.CardDescription>
        </div>
      </S.CardContent>
    </S.Card>
  );
};

export default CurrentWeather;
