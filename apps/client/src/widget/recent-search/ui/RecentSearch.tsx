import { useState } from 'react';
import { LocationItem } from '@/entities/location/ui/LocationItem';
import SEARCH_LIST_MOCK from '@/shared/mock/searchData.mock';

interface RecentSearchProps {
    onSelect: (name: string) => void;
    maxItems?: number;
}

/**
 * 최근 검색어 목록을 보여주는 위젯 (개별 삭제 기능 포함)
 */
const RecentSearch = ({ onSelect, maxItems = 3 }: RecentSearchProps) => {
    const [recentSearches, setRecentSearches] = useState(SEARCH_LIST_MOCK.slice(0, maxItems));

    const handleClearAll = () => {
        if (confirm('최근 검색어를 모두 삭제할까요?')) {
            setRecentSearches([]);
        }
    };

    const handleDeleteItem = (e: React.MouseEvent, id: string | number) => {
        e.stopPropagation(); // 카드 전체 클릭 이벤트 전파 방지
        setRecentSearches((prev) => prev.filter((item) => item.id !== id));
    };

    return (
        <div className="flex flex-col gap-4 mt-4">
            {/* 상단 헤더 영역 */}
            <div className="flex items-center justify-between px-1">
                <h2 className="text-lg font-bold text-foreground">최근 검색어</h2>
                {recentSearches.length > 0 && (
                    <button
                        onClick={handleClearAll}
                        className="text-xs text-muted-foreground hover:text-primary transition-colors active:scale-95"
                    >
                        전체 삭제
                    </button>
                )}
            </div>

            {/* 최근 검색어 목록 (개별 카드들) */}
            <div className="flex flex-col gap-3">
                {recentSearches.length > 0 ? (
                    recentSearches.map((location) => (
                        <div key={location.id} className="relative group">
                            <LocationItem
                                location={location}
                                onSelect={onSelect}
                                className="bg-white rounded-2xl shadow-sm border border-border/40 py-5 px-6 hover:bg-slate-50 border-b-transparent last:border-b-transparent pr-12"
                            />
                            {/* 개별 삭제 버튼 */}
                            <button
                                onClick={(e) => handleDeleteItem(e, location.id)}
                                className="absolute right-4 top-1/2 -translate-y-1/2 p-2 rounded-full hover:bg-muted text-muted-foreground hover:text-destructive transition-colors active:scale-90"
                                title="삭제"
                            >
                                <svg
                                    width="16"
                                    height="16"
                                    viewBox="0 0 24 24"
                                    fill="none"
                                    stroke="currentColor"
                                    strokeWidth="2.5"
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                >
                                    <path d="M18 6L6 18M6 6l12 12" />
                                </svg>
                            </button>
                        </div>
                    ))
                ) : (
                    <div className="flex flex-col items-center justify-center p-12 bg-white/40 rounded-3xl border border-dashed border-border/60">
                        <p className="text-sm text-muted-foreground italic">최근 검색한 장소가 없어요.</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default RecentSearch;
