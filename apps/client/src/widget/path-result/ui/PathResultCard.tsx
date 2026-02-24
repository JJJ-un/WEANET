import * as S from '@/shared/ui/Card';
import { useNavigate } from '@tanstack/react-router';
import { type PathResult, type TransportType } from '@/entities/path/model/types';
import SubwayIcon from '@/shared/assets/icons/subway.svg?react';
import { cn } from '@/shared/lib/utils';
import { usePathStore } from '@/entities/path/model/usePathStore';

interface PathResultCardProps {
    path: PathResult;
}

/**
 * 이동 수단 아이콘 매핑
 */
const TRANSPORT_ICONS: Record<string, React.FC<React.SVGProps<SVGSVGElement>>> = {
    SUBWAY: SubwayIcon,
    BUS: SubwayIcon,
    WALK: SubwayIcon,
};

/**
 * 이동 수단별 색상 매핑
 */
const TRANSPORT_COLORS: Record<string, string> = {
    SUBWAY: 'bg-primary',
    BUS: 'bg-secondary',
    WALK: 'bg-slate-300',
};

export const PathResultCard = ({ path }: PathResultCardProps) => {
    const navigate = useNavigate();
    const { setSelectedPath } = usePathStore();

    const handleCardClick = () => {
        setSelectedPath(path);
        navigate({
            to: '/path-search/detail/$pathId',
            params: { pathId: path.id || 'current' },
        });
    };

    // 아이콘과 색상을 안전하게 가져오는 헬퍼 함수
    const getTransportIcon = (type: string) => TRANSPORT_ICONS[type.toUpperCase()] || SubwayIcon;
    const getTransportColor = (type: string) => TRANSPORT_COLORS[type.toUpperCase()] || 'bg-slate-400';

    return (
        <div
            onClick={handleCardClick}
            className="block active:scale-[0.98] transition-all duration-200 cursor-pointer"
        >
            <S.Card className="p-6 bg-white rounded-3xl shadow-sm border border-border/50 hover:border-primary/20 hover:shadow-md transition-all">
                {/* 1. 라벨 섹션 */}
                <div className="flex gap-2 mb-4">
                    {(path.labels || ['추천']).map((label) => (
                        <span
                            key={label}
                            className={cn(
                                'px-2.5 py-1 rounded-lg text-[10px] font-extrabold',
                                label === '최적' ? 'bg-primary text-white' : 'bg-muted text-muted-foreground border border-border/50',
                            )}
                        >
                            {label}
                        </span>
                    ))}
                </div>

                <div className="flex flex-col gap-5">
                    {/* 2. 시간 섹션 */}
                    <div className="flex items-end gap-2">
                        <span className="text-4xl font-bold text-foreground leading-none">{path.totalTime ?? 0}</span>
                        <span className="text-sm font-bold text-foreground mb-1">분</span>
                        {path.arrivalTime && (
                            <span className="text-base text-muted-foreground mb-1 ml-1 font-medium">{path.arrivalTime} 도착</span>
                        )}
                    </div>

                    {/* 3. 요약 섹션 */}
                    <div className="flex items-center gap-2.5 text-xs font-bold text-muted-foreground">
                        {path.walkDuration && (
                            <>
                                <span>도보 {path.walkDuration}분</span>
                                <span className="text-border/60">|</span>
                            </>
                        )}
                        <span>{(path.totalFare ?? 0).toLocaleString()}원</span>
                        <span className="text-border/60">|</span>
                        <span>환승 {path.transferCount ?? 0}회</span>
                    </div>

                    {/* 4. 통합 경로 섹션 (컬러 바 + 중앙 정렬 아이콘) */}
                    <div className="flex flex-col mt-2">
                        {/* 상단: 아이콘 및 노선명 (컬러 바 구간 중앙 정렬) */}
                        <div className="flex w-full mb-2">
                            {(path.steps || []).map((step, index) => (
                                <div
                                    key={`label-${index}`}
                                    style={{ width: `${((step.sectionTime ?? 0) / (path.totalTime || 1)) * 100}%` }}
                                    className="flex justify-center items-center px-0.5"
                                >
                                    {step.transportType?.toUpperCase() !== 'WALK' && (
                                        <div className="flex items-center gap-1 overflow-hidden">
                                            <div className="shrink-0">
                                                {(() => {
                                                    const Icon = getTransportIcon(step.transportType || '');
                                                    return <Icon className="size-3 text-muted-foreground" />;
                                                })()}
                                            </div>
                                            <span className="text-[9px] font-extrabold text-muted-foreground truncate uppercase">
                                                {step.lineName}
                                            </span>
                                        </div>
                                    )}
                                </div>
                            ))}
                        </div>

                        {/* 하단: 컬러 바 (내부에 'XX분' 표시) */}
                        <div className="flex w-full h-6 rounded-xl overflow-hidden bg-slate-100 shadow-inner border border-white">
                            {(path.steps || []).map((step, index) => (
                                <div
                                    key={`bar-${index}`}
                                    className={cn(
                                        'flex items-center justify-center text-[9px] font-black text-white border-r border-white/20 last:border-r-0',
                                        getTransportColor(step.transportType || ''),
                                    )}
                                    style={{ width: `${((step.sectionTime ?? 0) / (path.totalTime || 1)) * 100}%` }}
                                >
                                    {/* 충분한 너비가 있을 때만 '분' 포함 표시 */}
                                    {(step.sectionTime ?? 0) > 5 ? (
                                        <span>{step.sectionTime}분</span>
                                    ) : (step.sectionTime ?? 0) > 2 ? (
                                        <span>{step.sectionTime}</span>
                                    ) : null}
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </S.Card>
        </div>
    );
};

export default PathResultCard;
