import { cn } from '@/shared/lib/utils';
import { type DetailPathStep } from '@/entities/path/model/types';
import { CongestionGauge } from '@/entities/path/ui/CongestionGauge';
import { getSubwayStyle } from '@/entities/path/lib/congestion';
import CongestionIcon from '@/shared/assets/icons/congestion.svg?react';

interface CongestionSectionProps {
    steps: DetailPathStep[];
}

export const CongestionSection = ({ steps }: CongestionSectionProps) => {
    const transitSteps = steps.filter((s) => s.transportType === 'SUBWAY');

    if (transitSteps.length === 0) return null;

    return (
        <section className="flex flex-col gap-4">
            <div className="flex items-center justify-between px-1">
                <div className="flex items-center gap-2">
                    <CongestionIcon className="w-6 h-6 text-primary" />
                    <h3 className="text-sm font-extrabold text-foreground">실시간 혼잡도 정보</h3>
                </div>
                <span className="text-[10px] font-bold text-muted-foreground bg-muted px-2 py-0.5 rounded-lg border border-border/50">
                    {transitSteps.length}개 구간
                </span>
            </div>

            {/* 가로 스크롤 캐러셀 영역 */}
            <div className="flex gap-4 overflow-x-auto pb-4 [ms-overflow-style:none] [scrollbar-width:none] [&::-webkit-scrollbar]:hidden -mx-6 px-6">
                {transitSteps.map((step, idx) => {
                    const subwayStyle = getSubwayStyle(step.lineName);
                    
                    return (
                        <div
                            key={idx}
                            className="bg-white min-w-[160px] p-6 rounded-3xl border border-border/40 shadow-sm flex flex-col gap-5 relative overflow-hidden"
                        >
                            <div className="flex justify-between items-start">
                                <div className="flex items-center gap-2">
                                    <div className={cn('size-2.5 rounded-full shrink-0', subwayStyle.bg)} />
                                    <span className="text-xs font-semibold text-foreground">
                                        {step.startStationName} ({step.lineName.replace(/수도권|서울/g, '').trim()})
                                    </span>
                                </div>
                            </div>

                            {/* 하단 상태 바 (에너지바 스타일) */}
                            <div className="flex flex-col gap-2 mt-1">
                                <CongestionGauge congestion={step.congestion} />
                            </div>
                        </div>
                    );
                })}
            </div>
        </section>
    );
};
