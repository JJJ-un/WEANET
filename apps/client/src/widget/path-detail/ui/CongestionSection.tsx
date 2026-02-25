import { cn } from '@/shared/lib/utils';
import { type DetailPathStep } from '@/entities/path/model/types';
import { CongestionGauge } from '@/entities/path/ui/CongestionGauge';
import CongestionIcon from '@/shared/assets/icons/congestion.svg?react';

interface CongestionSectionProps {
    steps: DetailPathStep[];
}

const getLineColor = (lineName: string) => {
    if (lineName.includes('1호선')) return 'bg-[#0052A4]';
    if (lineName.includes('2호선')) return 'bg-[#00A84D]';
    if (lineName.includes('3호선')) return 'bg-[#EF7C1C]';
    if (lineName.includes('4호선')) return 'bg-[#00A4E3]';
    if (lineName.includes('5호선')) return 'bg-[#996CAC]';
    if (lineName.includes('6호선')) return 'bg-[#CD7C2F]';
    if (lineName.includes('7호선')) return 'bg-[#747F00]';
    if (lineName.includes('8호선')) return 'bg-[#E6186C]';
    if (lineName.includes('9호선')) return 'bg-[#BDB092]';
    if (lineName.includes('수인분당')) return 'bg-[#FABE00]';
    if (lineName.includes('신분당')) return 'bg-[#D4003B]';
    if (lineName.includes('경의중앙')) return 'bg-[#77C4A3]';
    if (lineName.includes('경춘')) return 'bg-[#178C72]';
    if (lineName.includes('공항')) return 'bg-[#0090D2]';
    return 'bg-primary';
};

export const CongestionSection = ({ steps }: CongestionSectionProps) => {
    const transitSteps = steps.filter((s) => s.transportType === 'SUBWAY');

    if (transitSteps.length === 0) return null;

    return (
        <section className="flex flex-col gap-4">
            <div className="flex items-center justify-between px-1">
                <div className="flex items-center gap-3">
                    {/**아이콘 위치할 예정 */}
                    <CongestionIcon className="w-6 h-6 text-secondary" />
                    <h3 className="text-sm text-foreground">실시간 혼잡도 정보</h3>
                </div>
                <span className="text-[10px] font-bold text-muted-foreground bg-muted px-2 py-0.5 rounded-lg">
                    {transitSteps.length}개 구간
                </span>
            </div>

            {/* 가로 스크롤 캐러셀 영역 */}
            <div className="flex gap-4 overflow-x-auto pb-4 [ms-overflow-style:none] [scrollbar-width:none] [&::-webkit-scrollbar]:hidden -mx-6 px-6">
                {transitSteps.map((step, idx) => (
                    <div
                        key={idx}
                        className="bg-white min-w-[160px] p-6 rounded-3xl border border-border/40 shadow-sm flex flex-col gap-5.5 relative overflow-hidden"
                    >
                        <div className="flex justify-between items-start">
                            <div className="flex items-center gap-2">
                                <div className={cn('size-2.5 rounded-full shrink-0', getLineColor(step.lineName))} />
                                <span className="text-xs font-semibold text-foreground">
                                    {step.startStationName} ({step.lineName})
                                </span>
                            </div>
                        </div>

                        {/* 하단 상태 바 (에너지바 스타일) */}
                        <div className="flex flex-col gap-2 mt-1">
                            <CongestionGauge congestion={step.congestion} />
                        </div>
                    </div>
                ))}
            </div>
        </section>
    );
};
