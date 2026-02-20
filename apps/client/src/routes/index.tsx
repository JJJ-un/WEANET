import { createFileRoute } from '@tanstack/react-router';
import CurrentWeather from '@/widget/current-weather/ui/CurrentWeather';
import DailyForecast from '@/widget/current-weather/ui/DailyForecast';
import GroupInfo from '@/widget/group-info/ui/GroupInfo';

export const Route = createFileRoute('/')({
    component: RouteComponent,
});

function RouteComponent() {
    const hourlyData = [
        { time: '지금', status: 'Clear', temp: 24 },
        { time: '13시', status: 'Clear', temp: 25 },
        { time: '14시', status: 'Cloudy', temp: 25 },
        { time: '15시', status: 'Cloudy', temp: 24 },
        { time: '16시', status: 'Rain', temp: 22 },
        { time: '17시', status: 'Rain', temp: 21 },
    ] as const;

    return (
        <div className="flex flex-col gap-8 pb-32">
            {/* 1. 현재 날씨 정보 */}
            <CurrentWeather />

            {/* 2. 시간별 예보 리스트 (가로 스크롤) */}
            <div className="flex gap-4 overflow-x-auto pb-4 scrollbar-hide">
                {hourlyData.map((data, index) => (
                    <DailyForecast key={index} {...data} />
                ))}
            </div>
            {/* 3. 출근길 혼잡도 및 사용자 제보 */}
            <GroupInfo />
        </div>
    );
}
