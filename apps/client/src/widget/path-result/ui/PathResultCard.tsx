import * as S from '@/shared/ui/Card';
import { useNavigate } from '@tanstack/react-router';
import { type DetailPathResult } from '@/entities/path/model/types';
import { getArrivalTime } from '@/shared/lib/utils';
import { usePathStore } from '@/entities/path/model/usePathStore';
import { PathLabelSection } from './PathLabelSection';
import { PathSummaryInfo } from './PathSummaryInfo';
import { PathProgressBar } from './PathProgressBar';

interface PathResultCardProps {
    path: DetailPathResult;
    index: number;
}

export const PathResultCard = ({ path, index }: PathResultCardProps) => {
    const navigate = useNavigate();
    const { setSelectedPath } = usePathStore();

    // 도보 시간 계산
    const walkDuration = path.steps
        ? path.steps.filter((step) => step.transportType === 'WALK').reduce((acc, step) => acc + (step.sectionTime || 0), 0)
        : 0;

    const arrivalTime = getArrivalTime(path.totalTime || 0);
    const labels = index === 0 ? ['최적'] : [];

    const handleCardClick = () => {
        setSelectedPath(path);
        navigate({
            to: '/path-search/detail/$pathId',
            params: { pathId: index.toString() },
        });
    };

    return (
        <div onClick={handleCardClick} className="block active:scale-[0.98] transition-all duration-200 cursor-pointer">
            <S.Card className="p-6 bg-white rounded-3xl shadow-sm border border-border/50 hover:border-primary/20 hover:shadow-md transition-all flex flex-col gap-5">
                {/* 1. 상단 라벨 섹션 */}
                <PathLabelSection labels={labels} />

                <div className="flex flex-col gap-5">
                    {/* 2. 시간 정보 섹션 */}
                    <div className="flex items-end gap-2">
                        <span className="text-3xl font-semibold text-foreground">{path.totalTime ?? 0}분</span>
                        <span className="text-sm text-muted-foreground ml-1 font-medium">{arrivalTime} 도착</span>
                    </div>

                    {/* 3. 경로 요약 정보 섹션 */}
                    <PathSummaryInfo walkDuration={walkDuration} fare={path.totalFare ?? 0} transferCount={path.transferCount ?? 0} />

                    {/* 4. 통합 시각적 타임라인 바 섹션 */}
                    <PathProgressBar steps={path.steps} totalTime={path.totalTime} />
                </div>
            </S.Card>
        </div>
    );
};

export default PathResultCard;
