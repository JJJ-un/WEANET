import { createFileRoute } from '@tanstack/react-router';
import PathsSearchHeader from '@/widget/path-search-header/ui/PathSearchHeader';
import PathsSearchBar from '@/widget/path-search/ui/PathSearchBar';
import RecentSearch from '@/widget/recent-search/ui/RecentSearch';
import { PathResultCard } from '@/widget/path-result/ui/PathResultCard';
import { usePathSearch } from '@/entities/path/model/queries';

export const Route = createFileRoute('/path-search/')({
    component: PathSearchPage,
});

function PathSearchPage() {
    const { results, isLoading, isError, handleSearch, showResults } = usePathSearch();

    const handleSelectLocation = (name: string) => {
        console.log('장소 선택:', name);
    };

    return (
        <div className="flex flex-col gap-8 pb-32">
            {/* 1. 헤더 섹션 */}
            <PathsSearchHeader />

            {/* 2. 경로 탐색 바 위젯 */}
            <PathsSearchBar onSearch={handleSearch} />

            {/* 3. 하단 동적 섹션 (결과 또는 최근검색어) */}
            <div className="animate-in fade-in slide-in-from-bottom-4 duration-500">
                {showResults ? (
                    <div className="flex flex-col gap-5 mt-4">
                        <div className="flex justify-between items-center px-1">
                            <h2 className="text-lg font-bold text-foreground">
                                {isLoading ? '경로를 찾는 중...' : '경로 검색 결과'}
                            </h2>
                        </div>

                        {isLoading && (
                            <div className="py-20 text-center text-muted-foreground animate-pulse font-medium">
                                최적의 지하철 경로를 분석하고 있어요...
                            </div>
                        )}

                        {isError && (
                            <div className="py-20 text-center text-destructive font-medium">
                                경로 검색에 실패했습니다. 다시 시도해 주세요.
                            </div>
                        )}

                        {!isLoading && results.length > 0 ? (
                            results.map((path, index) => (
                                <PathResultCard key={index} path={path} />
                            ))
                        ) : (
                            !isLoading && <div className="py-20 text-center text-muted-foreground">일치하는 경로가 없습니다.</div>
                        )}
                    </div>
                ) : (
                    <RecentSearch onSelect={handleSelectLocation} maxItems={3} />
                )}
            </div>
        </div>
    );
}
