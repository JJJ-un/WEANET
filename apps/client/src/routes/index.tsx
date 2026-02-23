import { createFileRoute } from '@tanstack/react-router';
import LocationSelector from '@/features/select-location/ui/LocationSelector';
import CurrentWeather from '@/widget/current-weather/ui/CurrentWeather';
import DailyForecast from '@/widget/current-weather/ui/DailyForecast';
import GroupInfo from '@/widget/group-info/ui/GroupInfo';
import { useWeatherStore } from '@/entities/weather/model/useWeatherStore';
import { useWeather } from '@/entities/weather/model/queries';

export const Route = createFileRoute('/')({
    component: RouteComponent,
});

function RouteComponent() {
    const { selectedLocation } = useWeatherStore();
    const { data: weatherData, isLoading } = useWeather(selectedLocation);

    const hourlyData = weatherData?.hourlyForecast || [];

    return (
        <div className="flex flex-col gap-8 pb-32">
            {/* 0. 지역 선택 버튼 리스트 */}
            <LocationSelector />

            {/* 1. 현재 날씨 정보 (내부적으로 useWeather 호출 중) */}
            <CurrentWeather />

            {/* 2. 시간별 예보 리스트 (가로 스크롤) */}
            <div className="flex gap-4 overflow-x-auto pb-4 [ms-overflow-style:none] [scrollbar-width:none] [&::-webkit-scrollbar]:hidden snap-x snap-mandatory">
                {isLoading ? (
                    <div className="w-full py-10 text-center text-muted-foreground animate-pulse">
                        시간별 예보를 불러오는 중...
                    </div>
                ) : (
                    hourlyData.map((data, index) => (
                        <DailyForecast key={`${selectedLocation}-${index}`} {...data} />
                    ))
                )}
            </div>

            {/* 3. 출근길 혼잡도 및 사용자 제보 */}
            <GroupInfo />
        </div>
    );
}
