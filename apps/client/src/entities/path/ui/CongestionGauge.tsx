import { cn } from '@/shared/lib/utils';
import { CONGESTION_STYLE, getCongestionStatus } from '../lib/congestion';

interface CongestionGaugeProps {
    congestion: string | null | undefined;
    className?: string;
}

/**
 * 혼잡도 정보를 세 칸의 에너지 게이지 형태로 시각화하는 컴포넌트
 */
export const CongestionGauge = ({ congestion, className }: CongestionGaugeProps) => {
    const status = getCongestionStatus(congestion);
    const style = CONGESTION_STYLE[status ?? 'default'];

    return (
        <div className={cn('flex items-center justify-between w-full', className)}>
            <span className={cn('text-lg font-black', style.text)}>{congestion}</span>

            <div className="flex gap-1.5 w-16 h-8">
                {[1, 2, 3].map((level) => {
                    const isFilled = level <= style.fillLevel;

                    return (
                        <div
                            key={level}
                            className={cn('flex-1 rounded-[3px] transition-all duration-500', isFilled ? style.bg : 'bg-muted')}
                        />
                    );
                })}
            </div>
            {/** 추후 각 지역의 혼잡도를 표시하는 부분을 추가해야함(api 수정해야함) */}
        </div>
    );
};
