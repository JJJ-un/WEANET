import { createFileRoute } from "@tanstack/react-router";
import CurrentWeather from "@/widget/current-weather/ui/CurrentWeather";
import DailyForecast from "@/widget/current-weather/ui/DailyForecast";
import * as S from "@/shared/ui/Card";
import InfoIcon from "@/shared/assets/icons/info.svg?react";
import SubwayIcon from "@/shared/assets/icons/subway.svg?react";
import KebabMenuIcon from "@/shared/assets/icons/kebab-menu.svg?react";
import WarningIcon from "@/shared/assets/icons/warning.svg?react";

export const Route = createFileRoute("/")({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <div className="flex flex-col gap-8">
      <CurrentWeather />
      <div className="flex gap-4">
        <DailyForecast />
        <DailyForecast />
        <DailyForecast />
        <DailyForecast />
        <DailyForecast />
        <DailyForecast />
      </div>
      <S.Card className="p-6 mb-8">
        <S.CardHeader className="flex items-center justify-between gap-4">
          <div className="flex items-center gap-6">
            <SubwayIcon />
            <div>
              <S.CardTitle className="text-xl">출근길</S.CardTitle>
              <S.CardTitle className="text-xs text-muted-foreground">
                예상 혼잡도
              </S.CardTitle>
            </div>
          </div>
          <KebabMenuIcon />
        </S.CardHeader>
        <S.CardContent className="flex items-center justify-between gap-4 max-w-[33rem] bg-foreground rounded-xl p-4">
          <div className="flex items-center gap-4">
            <InfoIcon />
            <div className="text-sm">사용자 집단 정보, 우산 챙겨가세요</div>
          </div>
          <div className="text-xs text-muted-foreground">[34명 참여]</div>
        </S.CardContent>
        <S.CardContent className="flex items-center justify-between gap-4 max-w-[33rem] bg-foreground rounded-xl p-4">
          <div className="flex items-center gap-4">
            <InfoIcon />
            <div className="text-sm text-destructive">
              사고 발생: 강남대로 정체
            </div>
          </div>
          <div className="text-xs text-muted-foreground">[34명 참여]</div>
        </S.CardContent>
      </S.Card>
      <div>
        <p className="text-lg text-background mb-4">날씨 뉴스 피드</p>

        {/* 날씨 뉴스 피드 */}
        <div>
          <div className="flex items-center gap-4 mt-8">
            <WarningIcon className="inline" />
            <div className="flex flex-col gap-1">
              <span className="text-sm text-background">
                서울시, 미세먼지 주의보 발령. 외출 시 마스크 착용 권장
              </span>
              <span className="text-xs text-muted-foreground">연합뉴스</span>
            </div>
          </div>
          <div className="flex items-center gap-4 mt-8">
            <WarningIcon className="inline" />
            <div className="flex flex-col gap-1">
              <span className="text-sm text-background">
                서울시, 미세먼지 주의보 발령. 외출 시 마스크 착용 권장
              </span>
              <span className="text-xs text-muted-foreground">연합뉴스</span>
            </div>
          </div>
          <div className="flex items-center gap-4 mt-8">
            <WarningIcon className="inline" />
            <div className="flex flex-col gap-1">
              <span className="text-sm text-background">
                서울시, 미세먼지 주의보 발령. 외출 시 마스크 착용 권장
              </span>
              <span className="text-xs text-muted-foreground">연합뉴스</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
