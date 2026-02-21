import { useState } from 'react';
import { useLocationSearch } from '@/entities/location/model/useLocationSearch';
import { SearchField, type FieldType } from './SearchField';
import { SearchList } from './SearchList';

interface PathState {
    departure: string;
    destination: string;
}

/**
 * 메인 경로 탐색 바 위젯
 */
const PathsSearchBar = () => {
    const [paths, setPaths] = useState<PathState>({
        departure: '',
        destination: '',
    });
    const [activeField, setActiveField] = useState<FieldType | null>(null);

    const activeValue = activeField ? paths[activeField] : '';
    const { results } = useLocationSearch(activeValue);

    const handleInputChange = (field: FieldType, value: string) => {
        setPaths((prev) => ({ ...prev, [field]: value }));
    };

    const handleSelect = (name: string) => {
        if (activeField) handleInputChange(activeField, name);
        setActiveField(null);
    };

    const handleSwap = () => {
        setPaths({
            departure: paths.destination,
            destination: paths.departure,
        });
    };

    const isShowList = activeField !== null && activeValue.trim().length > 0;

    return (
        <div className="relative w-full max-w-[35rem] mx-auto">
            {/* 메인 입력 카드 */}
            <div className="flex flex-col gap-5 p-6 bg-white rounded-3xl shadow-xl border border-border/50 relative">
                <SearchField
                    type="departure"
                    value={paths.departure}
                    onChange={(val) => handleInputChange('departure', val)}
                    onFocus={() => setActiveField('departure')}
                    onBlur={() => setTimeout(() => setActiveField(null), 200)}
                />

                {/* 구분선 및 스왑 버튼 */}
                <div className="relative h-px bg-border/60 mx-10 z-10">
                    <button
                        onClick={handleSwap}
                        className="absolute right-[-12px] top-1/2 -translate-y-1/2 bg-white border border-border p-1.5 rounded-full shadow-sm hover:rotate-180 transition-transform duration-300 active:scale-90 z-20"
                        title="출발지/도착지 전환"
                    >
                        <svg
                            width="16"
                            height="16"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2.5"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            className="text-muted-foreground"
                        >
                            <path d="M7 16V4M7 4L3 8M7 4L11 8M17 8V20M17 20L21 16M17 20L13 16" />
                        </svg>
                    </button>
                </div>

                <SearchField
                    type="destination"
                    value={paths.destination}
                    onChange={(val) => handleInputChange('destination', val)}
                    onFocus={() => setActiveField('destination')}
                    onBlur={() => setTimeout(() => setActiveField(null), 200)}
                />
            </div>

            {/* 검색 결과 리스트 (팝업) */}
            {isShowList && <SearchList results={results} onSelect={handleSelect} />}
        </div>
    );
};

export default PathsSearchBar;
