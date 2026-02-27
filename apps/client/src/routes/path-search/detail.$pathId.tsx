import { createFileRoute } from '@tanstack/react-router';
import PathsSearchHeader from '@/widget/path-search-header/ui/PathSearchHeader';
import { usePathStore } from '@/entities/path/model/usePathStore';
import { MOCK_DETAIL_PATH_RESULT } from '@/shared/mock/pathData.mock';
import { type DetailPathResult } from '@/entities/path/model/types';
import { PathResultCard } from '@/widget/path-result/ui/PathResultCard';
import { CongestionSection } from '@/widget/path-detail/ui/CongestionSection';
import { WeatherSection } from '@/widget/path-detail/ui/WeatherSection';
import { TimelineSection } from '@/widget/path-detail/ui/TimelineSection';

export const Route = createFileRoute('/path-search/detail/$pathId')({
    component: PathSearchDetailPage,
});

function PathSearchDetailPage() {
    const { selectedPath } = usePathStore();

    // 현재는 API 연동 전이므로 Mock 상세 데이터를 사용합니다.
    const detailData: DetailPathResult = MOCK_DETAIL_PATH_RESULT;

    return (
        <div className="flex flex-col gap-6 pb-32 bg-slate-50/50 min-h-screen">
            <PathsSearchHeader />

            <div className="flex flex-col gap-10">
                {/* 1. 요약 카드 섹션 */}
                <div className="px-1 pointer-events-none">
                    <PathResultCard path={detailData} index={0} />
                </div>

                <div className="flex flex-col gap-10 px-1">
                    {/* 2. 실시간 혼잡도 섹션 */}
                    <CongestionSection steps={detailData.steps} />

                    {/* 3. 구간별 날씨 섹션 */}
                    <WeatherSection steps={detailData.steps} />

                    {/* 4. 상세 경로 안내 섹션 */}
                    <TimelineSection steps={detailData.steps} totalTime={detailData.totalTime} />
                </div>
            </div>
        </div>
    );
}

export default PathSearchDetailPage;
