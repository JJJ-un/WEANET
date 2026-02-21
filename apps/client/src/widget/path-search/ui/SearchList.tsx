import { LocationItem } from '@/entities/location/ui/LocationItem';
import { type LocationItem as LocationItemType } from '@/entities/location/model/useLocationSearch';

interface SearchListProps {
    results: LocationItemType[];
    onSelect: (name: string) => void;
}

/**
 * 검색 목록 팝업 컴포넌트
 */
export const SearchList = ({ results, onSelect }: SearchListProps) => {
    return (
        <div className="absolute top-[calc(100%+12px)] left-0 w-full bg-white/95 backdrop-blur-md border border-border shadow-2xl rounded-2xl z-[100] overflow-hidden animate-in fade-in slide-in-from-top-2 duration-200">
            {results.length > 0 ? (
                <div className="flex flex-col">
                    {results.map((item) => (
                        <LocationItem key={item.id} location={item} onSelect={onSelect} />
                    ))}
                </div>
            ) : (
                <div className="p-10 text-center">
                    <p className="text-sm text-muted-foreground italic">일치하는 장소를 찾을 수 없어요.</p>
                </div>
            )}
        </div>
    );
};
