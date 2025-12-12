import { createFileRoute } from "@tanstack/react-router";
import CurrentWeather from "@/widget/current-weather/ui/CurrentWeather";
import DailyForecast from "@/widget/current-weather/ui/DailyForecast";
import * as S from "@/shared/ui/card";
import InfoIcon from "@/shared/assets/icons/info.svg?react";

export const Route = createFileRoute("/")({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <div className="flex flex-col gap-4">
      <CurrentWeather />
      <DailyForecast />
      <S.Card>
        <S.CardHeader>
          <S.CardTitle>출근길</S.CardTitle>
        </S.CardHeader>
        <S.CardContent>
          <div>
            <div>
              <InfoIcon />
            </div>
            <div>사용자 집단 정보, 우산 챙겨가세요</div>
            <div>34명 참여</div>
          </div>
        </S.CardContent>
      </S.Card>
    </div>
  );
}
