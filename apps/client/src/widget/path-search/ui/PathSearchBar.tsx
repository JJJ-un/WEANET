import { useState, useEffect } from 'react';
import { useLocationSearch } from '@/entities/location/model/useLocationSearch';
import { SearchField, type FieldType } from './SearchField';
import { SearchList } from './SearchList';

interface PathState {
    departure: string;
    destination: string;
}

interface PathsSearchBarProps {
    onSearch?: (paths: PathState) => void;
    onClear?: () => void;
}

/**
 * 메인 경로 탐색 바 위젯
 */
const PathsSearchBar = ({ onSearch, onClear }: PathsSearchBarProps) => {
    const [paths, setPaths] = useState<PathState>({
        departure: '',
        destination: '',
    });
    const [activeField, setActiveField] = useState<FieldType | null>(null);

    const activeValue = activeField ? paths[activeField] : '';
    const { results } = useLocationSearch(activeValue);

    const handleInputChange = (field: FieldType, value: string) => {
        setPaths((prev) => ({ ...prev, [field]: value }));
        if (value === '' && onClear) onClear();
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

    // 두 필드가 모두 채워지면 부모에게 검색 실행 알림
    useEffect(() => {
        const isReady = paths.departure.trim().length > 0 && paths.destination.trim().length > 0;
        
        if (isReady && !activeField) {
            if (onSearch) onSearch(paths);
        }
    }, [paths, activeField, onSearch]);

    const isShowList = activeField !== null && activeValue.trim().length > 0;

    return (
        <div className="relative w-full max-w-[35rem] mx-auto">
            <div className="flex flex-col gap-5 p-6 bg-white rounded-3xl shadow-xl border border-border/50 relative">
                <SearchField
                    type="departure"
                    value={paths.departure}
                    onChange={(val) => handleInputChange('departure', val)}
                    onFocus={() => setActiveField('departure')}
                    onBlur={() => setTimeout(() => setActiveField(null), 200)}
                />

                <div className="relative h-px bg-border/60 mx-10 z-10">
                    <button
                        onClick={handleSwap}
                        className="absolute right-[-12px] top-1/2 -translate-y-1/2 bg-white border border-border p-1.5 rounded-full shadow-sm hover:rotate-180 transition-transform duration-300 active:scale-90 z-20"
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

            {isShowList && <SearchList results={results} onSelect={handleSelect} />}
        </div>
    );
};

export default PathsSearchBar;
