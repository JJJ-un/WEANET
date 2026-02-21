import { type LocationItem as LocationItemType } from '../model/useLocationSearch';

interface LocationItemProps {
    location: LocationItemType;
    onSelect: (name: string) => void;
    className?: string;
}

/**
 * 장소(Location) 정보를 리스트 형태로 보여주는 최소 단위 컴포넌트
 */
export const LocationItem = ({ location, onSelect, className }: LocationItemProps) => {
    return (
        <button
            onMouseDown={() => onSelect(location.name)}
            className={`flex flex-col items-start p-4 hover:bg-muted/50 transition-colors border-b last:border-0 text-left w-full ${className}`}
        >
            <span className="font-bold text-sm text-foreground">{location.name}</span>
            <span className="text-xs text-muted-foreground mt-1">{location.address}</span>
        </button>
    );
};
