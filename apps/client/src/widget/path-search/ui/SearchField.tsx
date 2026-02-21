import { cn } from '@/shared/lib/utils';
import { Input } from '@/shared/ui/input';
import DestinationIcon from '@/shared/assets/icons/destination.svg?react';
import DepartureIcon from '@/shared/assets/icons/departure.svg?react';

export type FieldType = 'departure' | 'destination';

interface SearchFieldProps {
    type: FieldType;
    value: string;
    onChange: (value: string) => void;
    onFocus: () => void;
    onBlur: () => void;
}

/**
 * 출발지/도착지 개별 입력 필드 컴포넌트
 */
export const SearchField = ({ type, value, onChange, onFocus, onBlur }: SearchFieldProps) => {
    const isDeparture = type === 'departure';
    const Icon = isDeparture ? DepartureIcon : DestinationIcon;
    const label = isDeparture ? '출발지' : '도착지';
    const placeholder = isDeparture ? '어디서 출발하시나요?' : '어디로 가시나요?';

    return (
        <div className="flex items-center gap-4 group">
            <Icon className={cn('shrink-0 transition-colors size-10', isDeparture ? 'text-primary' : 'text-secondary')} />
            <div className="flex-1">
                <p className="text-[11px] font-bold text-muted-foreground mb-1 ml-1 uppercase tracking-wider">{label}</p>
                <Input
                    value={value}
                    onChange={(e) => onChange(e.target.value)}
                    onFocus={onFocus}
                    onBlur={onBlur}
                    placeholder={placeholder}
                    className="w-full bg-white/50 border-none shadow-none focus-visible:ring-1 focus-visible:ring-border h-11 text-sm font-medium"
                />
            </div>
        </div>
    );
};
