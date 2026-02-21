import * as S from '@/shared/ui/Card';
import { WeatherIcon } from '@/entities/weather/ui/WeatherIcon';
import { WEATHER_CONFIG, type WeatherData } from '@/entities/weather/model/types';
import { useWeatherStore } from '@/entities/weather/model/useWeatherStore';

/**
 * 지역별 Mock 데이터 매핑
 */
const MOCK_WEATHER_DATA: Record<string, WeatherData> = {
    강남구: { status: 'Clear', currentTemp: 25, maxTemp: 28, minTemp: 18, precipitation: 10 },
    서초구: { status: 'Cloudy', currentTemp: 23, maxTemp: 26, minTemp: 17, precipitation: 20 },
    송파구: { status: 'Rain', currentTemp: 21, maxTemp: 24, minTemp: 16, precipitation: 80 },
    마포구: { status: 'Clear', currentTemp: 26, maxTemp: 29, minTemp: 19, precipitation: 0 },
    용산구: { status: 'Cloudy', currentTemp: 24, maxTemp: 27, minTemp: 18, precipitation: 15 },
    성동구: { status: 'Snow', currentTemp: -2, maxTemp: 1, minTemp: -5, precipitation: 90 },
    종로구: { status: 'Clear', currentTemp: 22, maxTemp: 25, minTemp: 15, precipitation: 5 },
};

const CurrentWeather = () => {
    const { selectedLocation } = useWeatherStore();

    // 선택된 지역의 데이터를 가져옴 (없으면 기본값 강남구)
    const weatherData = MOCK_WEATHER_DATA[selectedLocation] || MOCK_WEATHER_DATA['강남구'];
    const config = WEATHER_CONFIG[weatherData.status];

    return (
        <S.Card className="w-full flex flex-col items-center py-6 gap-2 bg-transparent shadow-none border-none text-foreground">
            {/* 지역명 표시 (추가됨) */}
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
        </S.Card>
    );
};

export default CurrentWeather;
