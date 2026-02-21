import * as S from '@/shared/ui/Card';
import { type PathResult } from '@/entities/path/model/types';
import SubwayIcon from '@/shared/assets/icons/subway.svg?react';
import InfoIcon from '@/shared/assets/icons/info.svg?react';

interface PathResultCardProps {
    path: PathResult;
}

/**
 * 경로 검색 결과 하나를 보여주는 카드 컴포넌트
 */
export const PathResultCard = ({ path }: PathResultCardProps) => {
    return (
        <S.Card className="p-6 bg-white rounded-3xl shadow-sm border border-border/50 hover:shadow-md transition-shadow cursor-pointer relative overflow-hidden">
            {path.isRecommended && (
                <div className="absolute top-0 left-0 bg-primary text-white text-[10px] font-bold px-3 py-1 rounded-br-xl">
                    최적 경로
                </div>
            )}

            <div className="flex flex-col gap-4">
                {/* 상단: 소요 시간 및 요약 */}
                <div className="flex items-end gap-2">
                    <span className="text-3xl font-bold text-foreground">{path.totalDuration}</span>
                    <span className="text-sm font-medium text-foreground mb-1">분</span>
                    <div className="ml-auto text-sm text-muted-foreground">
                        환승 {path.transferCount}회 | {path.fare?.toLocaleString()}원
                    </div>
                </div>

                {/* 중간: 이동 경로 요약 아이콘 바 */}
                <div className="flex items-center gap-2 py-2">
                    {path.steps.map((step, index) => (
                        <div key={index} className="flex items-center gap-2">
                            <div className="flex flex-col items-center">
                                <SubwayIcon className="size-5 text-muted-foreground" />
                                {step.lineName && <span className="text-[10px] mt-1">{step.lineName}</span>}
                            </div>
                            {index < path.steps.length - 1 && (
                                <div className="w-4 h-px bg-border mt-[-10px]" />
                            )}
                        </div>
                    ))}
                </div>

                {/* 하단: 날씨 팁 (우리 서비스의 핵심) */}
                {path.weatherTip && (
                    <div className="flex items-center gap-3 p-3 bg-secondary/10 rounded-2xl">
                        <InfoIcon className="size-4 text-secondary shrink-0" />
                        <p className="text-xs text-secondary-foreground font-medium">{path.weatherTip}</p>
                    </div>
                )}
            </div>
        </S.Card>
    );
};
