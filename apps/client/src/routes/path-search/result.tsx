import { createFileRoute } from '@tanstack/react-router';
import PathsSearchHeader from '@/widget/path-search-header/ui/PathSearchHeader';
import { PathResultCard } from '@/widget/path-result/ui/PathResultCard';
import { type PathResult } from '@/entities/path/model/types';

export const Route = createFileRoute('/path-search/result')({
    component: PathSearchResultPage,
});

/**
 * 경로 검색 결과 Mock 데이터
 */
const MOCK_PATH_RESULTS: PathResult[] = [
    {
        id: '1',
        totalDuration: 42,
        fare: 1550,
        transferCount: 1,
        isRecommended: true,
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
        fare: 1250,
        transferCount: 0,
        steps: [
            { type: 'bus', duration: 45, lineName: '9401번' },
            { type: 'walk', duration: 10 },
        ],
        weatherTip: '현재 경로 구간에 소나기 예보가 있으니 우산을 챙기세요.',
    },
    {
        id: '3',
        totalDuration: 48,
        fare: 1550,
        transferCount: 2,
        steps: [
            { type: 'subway', duration: 10, lineName: '3호선' },
            { type: 'subway', duration: 20, lineName: '신분당선' },
            { type: 'bus', duration: 18, lineName: '서초03' },
        ],
        weatherTip: '환승 구간이 실외이므로 강한 바람에 주의하세요.',
    },
];

function PathSearchResultPage() {
    return (
        <div className="flex flex-col gap-6 pb-32">
            {/* 상단 헤더: 뒤로가기 및 출발/도착지 요약 */}
            <PathsSearchHeader />

            {/* 필터 탭 (최적, 최단, 최소환승 등) */}
            <div className="flex gap-4 overflow-x-auto [ms-overflow-style:none] [scrollbar-width:none] [&::-webkit-scrollbar]:hidden py-2 -mx-6 px-6">
                {['최적', '최단시간', '최소환승', '최소도보'].map((filter, index) => (
                    <button
                        key={filter}
                        className={`px-6 py-2 rounded-full border text-sm font-medium whitespace-nowrap active:scale-95 transition-all ${
                            index === 0 ? 'bg-secondary text-white border-transparent' : 'bg-white border-border text-foreground'
                        }`}
                    >
                        {filter}
                    </button>
                ))}
            </div>

            {/* 경로 리스트 섹션 */}
            <div className="flex flex-col gap-5 mt-2">
                <div className="flex justify-between items-center px-1">
                    <p className="text-sm font-semibold text-muted-foreground">총 {MOCK_PATH_RESULTS.length}개의 경로</p>
                </div>

                {MOCK_PATH_RESULTS.map((path) => (
                    <PathResultCard key={path.id} path={path} />
                ))}
            </div>
        </div>
    );
}
