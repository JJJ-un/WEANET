import { cn } from '@/shared/lib/utils';
import { type PathStep } from '@/entities/path/model/types';
import { getSubwayStyle } from '@/entities/path/lib/congestion';
import SubwayIcon from '@/shared/assets/icons/subway.svg?react';
import BusIcon from '@/shared/assets/icons/bus.svg?react';
import WalkIcon from '@/shared/assets/icons/walk.svg?react';

interface PathProgressBarProps {
    steps: PathStep[];
    totalTime: number;
}

const TRANSPORT_ICONS: Record<string, React.FC<React.SVGProps<SVGSVGElement>>> = {
    SUBWAY: SubwayIcon,
    BUS: BusIcon,
    WALK: WalkIcon,
};

const TRANSPORT_COLORS: Record<string, string> = {
    SUBWAY: 'bg-primary',
    BUS: 'bg-secondary',
    WALK: 'bg-slate-300',
};

const getTransportIcon = (type: string) => TRANSPORT_ICONS[type.toUpperCase()] || SubwayIcon;

const getTransportStyles = (step: PathStep) => {
    const type = step.transportType?.toUpperCase();
    if (type === 'SUBWAY') {
        const style = getSubwayStyle(step.lineName);
        return { bg: style.bg, text: style.text, border: style.border };
    }
    const color = TRANSPORT_COLORS[type] || 'bg-slate-400';
    return {
        bg: color,
        text: 'text-muted-foreground',
        border: color.replace('bg-', 'border-'),
    };
};

export const PathProgressBar = ({ steps }: PathProgressBarProps) => (
    <div className="flex w-full mt-4 pb-2">
        {(steps || []).map((step: PathStep, idx: number) => {
            const styles = getTransportStyles(step);
            const Icon = getTransportIcon(step.transportType || '');

            return (
                <div key={idx} style={{ flex: step.sectionTime || 1 }} className="flex flex-col min-w-0 -ml-2">
                    {/* 상단: 아이콘 + 컬러 바 라인 */}
                    <div className="flex items-center w-full">
                        {/* 1. 노드 (아이콘) */}
                        <div
                            className={cn(
                                'size-8 rounded-full border-2 bg-white flex items-center justify-center z-10 shadow-sm shrink-0',
                                styles.border,
                            )}
                        >
                            <Icon className={cn('size-3', styles.text)} />
                        </div>

                        {/* 2. 컬러 바 (다음 노드까지 이어짐) */}
                        <div
                            className={cn(
                                'flex-1 h-5 -ml-1 rounded-r-full flex items-center justify-center overflow-hidden border-y border-r border-white/20',
                                styles.bg,
                            )}
                        >
                            {(step.sectionTime ?? 0) > 4 ? (
                                <span className="text-[8px] font-semibold text-white px-1 truncate ">{step.sectionTime}분</span>
                            ) : null}
                        </div>
                    </div>

                    {/* 하단: 텍스트 레이블 (아이콘 너비에 맞춰서 정렬) */}
                    <div className="mt-1 flex flex-col items-start px-0.5">
                        <span className={cn('text-[9px] font-black truncate max-w-full uppercase tracking-tighter', styles.text)}>
                            {step.transportType?.toUpperCase() === 'WALK'
                                ? '도보'
                                : step.transportType?.toUpperCase() === 'BUS'
                                  ? '버스'
                                  : step.lineName.replace(/수도권|서울/g, '').trim()}
                        </span>
                    </div>
                </div>
            );
        })}
    </div>
);
