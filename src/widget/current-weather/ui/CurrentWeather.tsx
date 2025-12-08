import * as S from "@/shared/ui/card";
import { Input } from "@/shared/ui/input";
import WeatherIcon from "@/shared/assets/icons/weather.svg?react";

const CurrentWeather = () => {
  // wiget은 피그마 ui 컴포넌트와 제일 가깝나?
  return (
    <S.Card className="w-[400px]">
      <S.CardHeader>
        <WeatherIcon />
        <S.CardTitle>카드 제목</S.CardTitle>
        <S.CardDescription>카드 설명</S.CardDescription>
      </S.CardHeader>
      <S.CardContent>
        <Input placeholder="입력하세요" />
      </S.CardContent>
      <S.CardFooter>카드 푸터</S.CardFooter>
    </S.Card>
  );
};

export default CurrentWeather;
