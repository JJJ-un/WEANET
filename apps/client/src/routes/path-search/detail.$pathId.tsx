import { createFileRoute } from '@tanstack/react-router';
import PathsSearchHeader from '@/widget/path-search-header/ui/PathSearchHeader';
import SubwayIcon from '@/shared/assets/icons/subway.svg?react';
import InfoIcon from '@/shared/assets/icons/info.svg?react';

export const Route = createFileRoute('/path-search/detail/$pathId')({
    component: PathSearchDetailPage,
});

function PathSearchDetailPage() {
    const { pathId } = Route.useParams();

    return (
        <div className="flex flex-col gap-8 pb-32">
            {/* 상단 헤더: 상세 정보임을 나타냄 */}
            <PathsSearchHeader />

            <div className="flex flex-col gap-6">
                {/* 핵심 요약 영역 */}
                <div className="bg-white rounded-3xl p-8 shadow-sm border border-border/40">
                    <div className="flex items-end gap-2 mb-6">
                        <span className="text-4xl font-bold text-foreground">42</span>
                        <span className="text-lg font-medium text-foreground mb-1.5">분</span>
                        <div className="ml-auto text-sm text-muted-foreground">최적 경로 (ID: {pathId})</div>
                    </div>

                    {/* 상세 단계 (Timeline 형태 예시) */}
                    <div className="flex flex-col gap-8 ml-2 border-l-2 border-dashed border-border pl-8 relative">
                        {/* 단계 1 */}
                        <div className="relative">
                            <div className="absolute -left-[41px] top-0 size-5 rounded-full bg-primary border-4 border-white shadow-sm" />
                            <div className="flex flex-col gap-1">
                                <span className="font-bold text-base">강남역 승차</span>
                                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                                    <SubwayIcon className="size-4" />
                                    <span>2호선 | 7개역 이동</span>
                                </div>
                            </div>
                        </div>

                        {/* 단계 2 (날씨 경고가 있는 구간) */}
                        <div className="relative">
                            <div className="absolute -left-[41px] top-0 size-5 rounded-full bg-secondary border-4 border-white shadow-sm" />
                            <div className="flex flex-col gap-2">
                                <span className="font-bold text-base">당산역 환승</span>
                                <div className="p-4 bg-secondary/10 rounded-2xl flex items-start gap-3">
                                    <InfoIcon className="size-4 text-secondary shrink-0 mt-0.5" />
                                    <p className="text-xs text-secondary-foreground leading-relaxed">
                                        이 구간은 지상역입니다. 현재 강한 소나기가 내리고 있으니 환승 시 젖지 않게 주의하세요.
                                    </p>
                                </div>
                            </div>
                        </div>

                        {/* 단계 3 */}
                        <div className="relative">
                            <div className="absolute -left-[41px] top-0 size-5 rounded-full bg-slate-400 border-4 border-white shadow-sm" />
                            <span className="font-bold text-base">홍대입구역 하차</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
