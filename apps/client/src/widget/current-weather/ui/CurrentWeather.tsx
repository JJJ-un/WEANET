import * as S from '@/shared/ui/Card';
import { WeatherIcon } from '@/entities/weather/ui/WeatherIcon';
import { WEATHER_CONFIG } from '@/entities/weather/model/types';
import { useWeatherStore } from '@/entities/weather/model/useWeatherStore';
import { useWeather } from '@/entities/weather/model/queries';

const CurrentWeather = () => {
    const { selectedLocation } = useWeatherStore();

    // 1. 실제 API 데이터 호출 (선택된 지역 이름 전달)
    // TODO: 지역 이름(강남구 등)을 서버가 이해하는 City 이름(Seoul 등)으로 변환하는 로직이 필요할 수 있습니다.
    const { data: weatherData, isLoading, isError } = useWeather(selectedLocation);

    // 2. 로딩 및 에러 상태 처리
    if (isLoading) return <div className="py-20 text-center text-muted-foreground animate-pulse">날씨 정보를 불러오는 중...</div>;
    if (isError || !weatherData) return <div className="py-20 text-center text-destructive">날씨 정보를 가져오지 못했습니다.</div>;

    const config = WEATHER_CONFIG[weatherData.status];

    return (
        <S.Card className="w-full flex flex-col items-center py-6 gap-2 bg-transparent shadow-none border-none text-foreground">
            {/* 지역명 표시 */}
            <div className="text-sm font-medium text-muted-foreground mb-1">{selectedLocation} 현재 날씨</div>

            {/* 1. WeatherIcon: 컴포넌트 중앙 상단 */}
            <div className="flex justify-center">
                <WeatherIcon status={weatherData.status} className="w-20 h-20" />
            </div>

            {/* 2. 기온 및 날씨 상태: 아이콘 아래 중앙 */}
            <div className="flex flex-col items-center">
                <span className="text-6xl font-light">{weatherData.currentTemp}°</span>
                <S.CardTitle className="text-lg font-normal mt-1 text-foreground">{config.label}</S.CardTitle>
            </div>

            {/* 3. 최고, 최저, 강수 확률: 그 밑에 배치 */}
            <div className="flex gap-3 text-sm mt-2 text-foreground">
                <div className="flex gap-2 font-medium">
                    <span>최고: {weatherData.maxTemp}°</span>
                    <span>최저: {weatherData.minTemp}°</span>
                </div>
                <span className="text-muted-foreground font-light">|</span>
                <div className="font-medium">
                    <span>강수 확률: {weatherData.precipitation}%</span>
                </div>
            </div>

            {/* 4. 날씨 조언 (Advice): 하단에 강조 */}
            {weatherData.advice && (
                <div className="mt-6 px-4 py-3 bg-white/50 backdrop-blur-sm rounded-2xl border border-white/20 text-sm text-center text-slate-600 leading-relaxed shadow-sm">
                    {weatherData.advice}
                </div>
            )}
        </S.Card>
    );
};

export default CurrentWeather;
