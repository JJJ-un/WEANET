import * as S from '@/shared/ui/Card';
import InfoIcon from '@/shared/assets/icons/info.svg?react';
import SubwayIcon from '@/shared/assets/icons/subway.svg?react';
import WeatherIcon from '@/shared/assets/icons/weather.svg?react';
import { type GroupInfoItem, type InfoCategory } from '../model/types';

interface InfoItemProps extends Omit<GroupInfoItem, 'id'> {
    className?: string;
}

/**
 * 카테고리에 따른 아이콘 매핑
 */
const CATEGORY_ICONS: Record<InfoCategory, React.FC<React.SVGProps<SVGSVGElement>>> = {
    subway: SubwayIcon,
    bus: SubwayIcon,
    'path-weather': WeatherIcon,
    'user-report': InfoIcon,
};

/**
 * 정보 항목 하나를 렌더링하는 컴포넌트
 */
export const InfoItem = ({ category, content, participants, isUrgent, className }: InfoItemProps) => {
    const Icon = CATEGORY_ICONS[category];

    return (
        <S.CardContent className={`flex items-center justify-between gap-4 bg-background rounded-xl p-4 text-forground ${className}`}>
            <div className="flex items-center gap-4">
                <Icon className="shrink-0 w-5 h-5" />
                <div className="text-sm">{isUrgent ? <span className="text-destructive font-bold">{content}</span> : content}</div>
            </div>
            {participants !== undefined && <div className="text-[10px] text-muted-foreground shrink-0">[{participants}명 참여]</div>}
        </S.CardContent>
    );
};
