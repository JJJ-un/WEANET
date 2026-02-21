import { Button } from '@/shared/ui/Button';
import { useWeatherStore } from '@/entities/weather/model/useWeatherStore';

/**
 * 가로 스크롤이 가능한 지역 선택 리스트 컴포넌트
 */
const LocationSelector = () => {
    const locations = ['강남구', '서초구', '송파구', '마포구', '용산구', '성동구', '종로구'];
    const { selectedLocation, setSelectedLocation } = useWeatherStore();

    return (
        <div className="w-full">
            <div className="flex gap-3 overflow-x-auto pb-2 [ms-overflow-style:none] [scrollbar-width:none] [&::-webkit-scrollbar]:hidden snap-x snap-mandatory">
                {locations.map((location) => (
                    <Button
                        key={location}
                        variant={selectedLocation === location ? 'selected' : 'outline'}
                        size="badge"
                        onClick={() => setSelectedLocation(location)}
                        className="snap-start" // 자식 요소에 스냅 기준점 추가
                    >
                        {location}
                    </Button>
                ))}
            </div>
        </div>
    );
};

export default LocationSelector;
