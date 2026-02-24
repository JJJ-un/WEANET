import { createFileRoute, useNavigate } from '@tanstack/react-router';
import { useQuery } from '@tanstack/react-query';
import PathsSearchHeader from '@/widget/path-search-header/ui/PathSearchHeader';
import SubwayIcon from '@/shared/assets/icons/subway.svg?react';
import { usePathStore } from '@/entities/path/model/usePathStore';
import { getRouteDetails } from '@/entities/path/api/pathApi';
import { useEffect } from 'react';
import { cn } from '@/shared/lib/utils';

export const Route = createFileRoute('/path-search/detail/$pathId')({
    component: PathSearchDetailPage,
});

function PathSearchDetailPage() {
    const { selectedPath } = usePathStore();
    const navigate = useNavigate();

    // 데이터가 없는 경우 (새로고침 등) 결과 페이지로 돌려보냄
    useEffect(() => {
        if (!selectedPath) {
            navigate({ to: '/path-search/result' });
        }
    }, [selectedPath, navigate]);

    const { data: detailData, isLoading, isError } = useQuery({
        queryKey: ['pathDetail', selectedPath],
        queryFn: () => getRouteDetails(selectedPath!),
        enabled: !!selectedPath,
    });

    if (isLoading) return <div className="py-20 text-center text-muted-foreground animate-pulse font-medium">상세 경로를 불러오는 중...</div>;
    if (isError || !detailData) return <div className="py-20 text-center text-destructive font-medium">상세 정보를 불러오는데 실패했습니다.</div>;

    return (
        <div className="flex flex-col gap-8 pb-32">
            <PathsSearchHeader />

            <div className="flex flex-col gap-6">
                <div className="bg-white rounded-3xl p-8 shadow-sm border border-border/40">
                    <div className="flex items-end gap-2 mb-6">
                        <span className="text-4xl font-bold text-foreground">{detailData.totalTime}</span>
                        <span className="text-lg font-medium text-foreground mb-1.5">분</span>
                        <div className="ml-auto text-sm text-muted-foreground font-bold text-primary">
                            {detailData.transferCount}회 환승 | {detailData.totalFare.toLocaleString()}원
                        </div>
                    </div>

                    {/* 상세 단계 (Timeline) */}
                    <div className="flex flex-col gap-8 ml-2 border-l-2 border-dashed border-border pl-8 relative">
                        {detailData.steps.map((step, index) => (
                            <div key={index} className="relative">
                                {/* 타임라인 점 */}
                                <div 
                                    className={cn(
                                        "absolute -left-[41px] top-0 size-5 rounded-full border-4 border-white shadow-sm",
                                        step.transportType === 'SUBWAY' ? 'bg-primary' : 
                                        step.transportType === 'BUS' ? 'bg-secondary' : 'bg-slate-300'
                                    )} 
                                />
                                
                                <div className="flex flex-col gap-1">
                                    <div className="flex justify-between items-start">
                                        <span className="font-bold text-base leading-tight">
                                            {step.startStationName} {step.transportType !== 'WALK' ? '승차' : ''}
                                        </span>
                                        <span className="text-xs font-bold text-muted-foreground bg-muted px-2 py-0.5 rounded">
                                            {step.sectionTime}분
                                        </span>
                                    </div>

                                    {step.transportType !== 'WALK' && (
                                        <div className="flex items-center gap-2 text-sm text-muted-foreground mt-1">
                                            <SubwayIcon className="size-4" />
                                            <span className="font-medium">{step.lineName}</span>
                                            <span className="text-border">|</span>
                                            <span>{step.endStationName} 방면</span>
                                        </div>
                                    )}

                                    {/* 마지막 도착지 표시 */}
                                    {index === detailData.steps.length - 1 && (
                                        <div className="mt-8 relative">
                                            <div className="absolute -left-[41px] top-0 size-5 rounded-full bg-foreground border-4 border-white shadow-sm" />
                                            <span className="font-bold text-base">{step.endStationName} 도착</span>
                                        </div>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default PathSearchDetailPage;
