import { useState } from 'react';
import { createFileRoute } from '@tanstack/react-router';
import PathsSearchHeader from '@/widget/path-search-header/ui/PathSearchHeader';
import PathsSearchBar from '@/widget/path-search/ui/PathSearchBar';
import RecentSearch from '@/widget/recent-search/ui/RecentSearch';
import { PathResultCard } from '@/widget/path-result/ui/PathResultCard';
import { type PathResult } from '@/entities/path/model/types';

export const Route = createFileRoute('/path-search/')({
    component: PathSearchPage,
});

/**
 * 최신 인터페이스(PathResult)에 맞춘 Mock 데이터
 */
const MOCK_RESULTS: PathResult[] = [
    {
        id: '1',
        totalDuration: 42,
        arrivalTime: '16:42',
        fare: 1550,
        transferCount: 1,
        walkDuration: 5,
        labels: ['최적', '추천'],
        steps: [
            { type: 'subway', duration: 15, lineName: '2호선' },
            { type: 'walk', duration: 5 },
            { type: 'subway', duration: 22, lineName: '9호선' },
        ],
        weatherTip: '이동 경로 중 비가 오지 않아 쾌적합니다.',
    },
    {
        id: '2',
        totalDuration: 55,
        arrivalTime: '16:55',
        fare: 1250,
        transferCount: 0,
        walkDuration: 10,
        labels: ['최소환승'],
        steps: [
            { type: 'bus', duration: 45, lineName: '9401번' },
            { type: 'walk', duration: 10 },
        ],
        weatherTip: '현재 경로 구간에 소나기 예보가 있으니 우산을 챙기세요.',
    },
];

function PathSearchPage() {
    const [showResults, setShowResults] = useState(false);

    const handleSearch = (paths: { departure: string; destination: string }) => {
        console.log('검색 실행:', paths);
        setShowResults(true);
    };

    const handleSelectLocation = (name: string) => {
        console.log('장소 선택:', name);
    };

    return (
        <div className="flex flex-col gap-8 pb-32">
            {/* 1. 헤더 섹션 */}
            <PathsSearchHeader />

            {/* 2. 경로 탐색 바 위젯 */}
            <PathsSearchBar onSearch={handleSearch} onClear={() => setShowResults(false)} />

            {/* 3. 하단 동적 섹션 (결과 또는 최근검색어) */}
            <div className="animate-in fade-in slide-in-from-bottom-4 duration-500">
                {showResults ? (
                    <div className="flex flex-col gap-5 mt-4">
                        <div className="flex justify-between items-center px-1">
                            <h2 className="text-lg font-bold text-foreground">경로 검색 결과</h2>
                            <button
                                onClick={() => setShowResults(false)}
                                className="text-xs text-muted-foreground hover:text-primary transition-colors"
                            >
                                닫기
                            </button>
                        </div>
                        {MOCK_RESULTS.map((path) => (
                            <PathResultCard key={path.id} path={path} />
                        ))}
                    </div>
                ) : (
                    <RecentSearch onSelect={handleSelectLocation} maxItems={3} />
                )}
            </div>
        </div>
    );
}
