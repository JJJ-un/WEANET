import { createFileRoute } from '@tanstack/react-router';
import LocationSelector from '@/features/select-location/ui/LocationSelector';
import CurrentWeather from '@/widget/current-weather/ui/CurrentWeather';
import DailyForecast from '@/widget/current-weather/ui/DailyForecast';
import GroupInfo from '@/widget/group-info/ui/GroupInfo';
import { useWeatherStore } from '@/entities/weather/model/useWeatherStore';
import { type HourlyForecast } from '@/entities/weather/model/types';

export const Route = createFileRoute('/')({
    component: RouteComponent,
});

/**
 * 지역별 시간별 예보 Mock 데이터
 */
const MOCK_HOURLY_DATA: Record<string, HourlyForecast[]> = {
    강남구: [
        { time: '지금', status: 'Clear', temp: 24 },
        { time: '13시', status: 'Clear', temp: 25 },
        { time: '14시', status: 'Cloudy', temp: 25 },
        { time: '15시', status: 'Cloudy', temp: 24 },
        { time: '16시', status: 'Rain', temp: 22 },
        { time: '17시', status: 'Rain', temp: 21 },
        { time: '18시', status: 'Cloudy', temp: 20 },
        { time: '19시', status: 'Clear', temp: 19 },
        { time: '20시', status: 'Clear', temp: 18 },
        { time: '21시', status: 'Clear', temp: 17 },
    ],
    서초구: [
        { time: '지금', status: 'Cloudy', temp: 23 },
        { time: '13시', status: 'Cloudy', temp: 24 },
        { time: '14시', status: 'Rain', temp: 22 },
        { time: '15시', status: 'Rain', temp: 21 },
        { time: '16시', status: 'Cloudy', temp: 22 },
        { time: '17시', status: 'Clear', temp: 23 },
        { time: '18시', status: 'Clear', temp: 22 },
        { time: '19시', status: 'Clear', temp: 20 },
        { time: '20시', status: 'Clear', temp: 19 },
        { time: '21시', status: 'Clear', temp: 18 },
    ],
    송파구: [
        { time: '지금', status: 'Rain', temp: 21 },
        { time: '13시', status: 'Rain', temp: 20 },
        { time: '14시', status: 'Rain', temp: 20 },
        { time: '15시', status: 'Cloudy', temp: 21 },
        { time: '16시', status: 'Cloudy', temp: 22 },
        { time: '17시', status: 'Clear', temp: 23 },
        { time: '18시', status: 'Clear', temp: 22 },
        { time: '19시', status: 'Clear', temp: 20 },
        { time: '20시', status: 'Cloudy', temp: 19 },
        { time: '21시', status: 'Rain', temp: 18 },
    ],
    성동구: [
        { time: '지금', status: 'Snow', temp: -2 },
        { time: '13시', status: 'Snow', temp: -1 },
        { time: '14시', status: 'Snow', temp: 0 },
        { time: '15시', status: 'Cloudy', temp: 1 },
        { time: '16시', status: 'Cloudy', temp: 2 },
        { time: '17시', status: 'Clear', temp: 3 },
        { time: '18시', status: 'Clear', temp: 2 },
        { time: '19시', status: 'Clear', temp: 0 },
        { time: '20시', status: 'Snow', temp: -1 },
        { time: '21시', status: 'Snow', temp: -2 },
    ],
};

function RouteComponent() {
    const { selectedLocation } = useWeatherStore();

    // 선택된 지역의 시간별 데이터를 가져옴 (없으면 강남구 기본값)
    const hourlyData = MOCK_HOURLY_DATA[selectedLocation] || MOCK_HOURLY_DATA['강남구'];

    return (
        <div className="flex flex-col gap-8 pb-32">
            {/* 0. 지역 선택 버튼 리스트 */}
            <LocationSelector />

            {/* 1. 현재 날씨 정보 */}
            <CurrentWeather />

            {/* 2. 시간별 예보 리스트 (가로 스크롤) */}
            <div className="flex gap-4 overflow-x-auto pb-4 [ms-overflow-style:none] [scrollbar-width:none] [&::-webkit-scrollbar]:hidden snap-x snap-mandatory">
                {hourlyData.map((data, index) => (
                    <DailyForecast key={`${selectedLocation}-${index}`} {...data} />
                ))}
            </div>

            {/* 3. 출근길 혼잡도 및 사용자 제보 */}
            <GroupInfo />
        </div>
    );
}
