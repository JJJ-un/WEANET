import * as S from '@/shared/ui/Card';
import { WeatherIcon } from '@/entities/weather/ui/WeatherIcon';
import { WEATHER_CONFIG, type WeatherData } from '@/entities/weather/model/types';

const CurrentWeather = () => {
    // 실제 API (TanStack Query 등) 연동 시 이 부분을 useQuery 결과로 대체
    const weatherData: WeatherData = {
        status: 'Clear',
        currentTemp: 25,
        maxTemp: 28,
        minTemp: 18,
        precipitation: 10,
    };

    const config = WEATHER_CONFIG[weatherData.status];

    return (
        <S.Card className="w-full flex flex-col items-center py-6 gap-2 bg-transparent shadow-none border-none text-foreground">
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
        </S.Card>
    );
};

export default CurrentWeather;
