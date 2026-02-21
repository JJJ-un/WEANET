interface SearchItem {
    id: number | string;
    name: string;
    address: string;
}

interface SearchResultListProps {
    results: SearchItem[];
    onSelect: (name: string) => void;
}

/**
 * 검색 결과 목록 팝업 컴포넌트
 */
export const SearchResultList = ({ results, onSelect }: SearchResultListProps) => {
    return (
        <div className="absolute top-[calc(100%+12px)] left-0 w-full bg-white/95 backdrop-blur-md border border-border shadow-2xl rounded-2xl z-[100] overflow-hidden animate-in fade-in slide-in-from-top-2 duration-200">
            {results.length > 0 ? (
                <div className="flex flex-col">
                    {results.map((item) => (
                        <button
                            key={item.id}
                            onMouseDown={() => onSelect(item.name)}
                            className="flex flex-col items-start p-4 hover:bg-muted/50 transition-colors border-b last:border-0 text-left w-full"
                        >
                            <span className="font-bold text-sm text-foreground">{item.name}</span>
                            <span className="text-xs text-muted-foreground mt-1">{item.address}</span>
                        </button>
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
