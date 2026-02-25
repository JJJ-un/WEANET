import { cn } from '@/shared/lib/utils';
import { type DetailPathStep } from '@/entities/path/model/types';
import RouteIcon from '@/shared/assets/icons/route.svg?react';
import { getLineColor } from '@/entities/path/lib/congestion';

interface TimelineSectionProps {
    steps: DetailPathStep[];
    totalTime: number;
}

export const TimelineSection = ({ steps, totalTime }: TimelineSectionProps) => {
    // 전체 대비 비중을 계산하기 위한 총 가용 높이 (컴팩트하게 조정)
    const TOTAL_FLEXIBLE_HEIGHT = 120;
    const BASE_HEIGHT = 48;

    return (
        <section className="flex flex-col gap-6 mb-20">
            <div className="flex items-center gap-2 px-1">
                <RouteIcon className="w-8 h-8 text-primary" />
                <h3 className="text-sm font-extrabold text-foreground">상세 경로 안내</h3>
            </div>

            <div className="flex flex-col pb-10">
                {steps.map((step, index) => {
                    const isSubway = step.transportType === 'SUBWAY';
                    const lineColor = getLineColor(step.lineName);
                    // 전체 시간 대비 해당 구간의 비중 계산 (0 ~ 1)
                    const timeRatio = totalTime > 0 ? step.sectionTime / totalTime : 0;
                    // 비중에 따른 동적 높이 결정 (컴팩트한 베이스 + 작은 가중치)
                    const dynamicHeight = BASE_HEIGHT + timeRatio * TOTAL_FLEXIBLE_HEIGHT;

                    return (
                        <div key={index} className="relative pl-10 last:pb-0" style={{ paddingBottom: `${dynamicHeight}px` }}>
                            {/* 지점 마커 (승차/출발) */}
                            <div
                                className={cn(
                                    'absolute left-0 top-1 size-[17px] rounded-full border-[3.5px] border-white shadow-sm z-10',
                                    index === 0 ? 'bg-slate-800' : isSubway ? lineColor : 'bg-slate-300',
                                )}
                            />
                            {/* 구간 연결 선 (모든 단계에서 아래로 연장) */}
                            <div
                                className={cn(
                                    'absolute left-[7px] top-6 w-[3px] bottom-0 z-0',
                                    isSubway
                                        ? lineColor // 지하철은 실선
                                        : 'border-l-[3px] border-dashed border-border bg-transparent', // 도보는 점선
                                )}
                            />

                            {/* 콘텐츠 영역 (승차/출발 정보) */}
                            <div className="flex flex-col gap-2">
                                <div className="flex justify-between items-start">
                                    <div className="flex gap-3">
                                        <span className="text-sm font-bold text-foreground">
                                            {step.startStationName} {index === 0 ? '출발' : isSubway ? '승차' : '하차'}
                                        </span>
                                        {isSubway && (
                                            <span
                                                className={cn(
                                                    'flex items-center justify-center w-fit text-[9px] px-1.5 py-0.5 rounded-full text-white font-bold',
                                                    lineColor,
                                                )}
                                            >
                                                {step.lineName}
                                            </span>
                                        )}
                                    </div>
                                    <span className="text-[10px] font-black text-muted-foreground bg-muted px-2 py-0.5 rounded-md border border-border/50">
                                        {step.sectionTime}분
                                    </span>
                                </div>
                                {isSubway && (
                                    <div className="flex flex-col gap-1 mt-1.5">
                                        <span className="text-[10px] text-muted-foreground font-bold pl-0.5">
                                            {step.endStationName} 방면
                                        </span>
                                    </div>
                                )}
                            </div>

                            {/* 마지막 단계의 최종 도착지 표시 */}
                            {index === steps.length - 1 && (
                                <div className="absolute bottom-0 left-0 right-0 pl-10 translate-y-1/2">
                                    <div className="absolute left-0 top-1/2 -translate-y-1/2 size-[17px] rounded-full bg-slate-800 border-[3.5px] border-white shadow-sm z-10" />
                                    <div className="flex flex-col gap-0.5">
                                        <span className="text-base font-extrabold text-foreground leading-tight">
                                            {step.endStationName} 도착
                                        </span>
                                        <span className="text-[10px] text-muted-foreground font-medium">총 {totalTime}분 소요</span>
                                    </div>
                                </div>
                            )}
                        </div>
                    );
                })}
            </div>
        </section>
    );
};
