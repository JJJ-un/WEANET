import { createFileRoute } from '@tanstack/react-router';
import { useQuery } from '@tanstack/react-query';
import PathsSearchHeader from '@/widget/path-search-header/ui/PathSearchHeader';
import { PathResultCard } from '@/widget/path-result/ui/PathResultCard';
import { searchRoutes } from '@/entities/path/api/pathApi';

interface PathSearchQueryParams {
    departureName: string;
    destinationName: string;
}

export const Route = createFileRoute('/path-search/result')({
    component: PathSearchResultPage,
    validateSearch: (search: Record<string, unknown>): PathSearchQueryParams => {
        return {
            departureName: (search.departureName as string) || '',
            destinationName: (search.destinationName as string) || '',
        };
    },
});

function PathSearchResultPage() {
    const { departureName, destinationName } = Route.useSearch();

    const { data: pathResults, isLoading, isError } = useQuery({
        queryKey: ['pathSearch', departureName, destinationName],
        queryFn: () => searchRoutes({ departureName, destinationName }),
        enabled: !!departureName && !!destinationName,
    });

    if (isLoading) return <div className="py-20 text-center text-muted-foreground animate-pulse font-medium">최적의 경로를 찾고 있어요...</div>;
    if (isError) return <div className="py-20 text-center text-destructive font-medium">경로를 불러오는데 실패했습니다.</div>;

    const results = pathResults || [];

    return (
        <div className="flex flex-col gap-6 pb-32">
            {/* 상단 헤더 */}
            <PathsSearchHeader />

            {/* 필터 탭 */}
            <div className="flex gap-4 overflow-x-auto [ms-overflow-style:none] [scrollbar-width:none] [&::-webkit-scrollbar]:hidden py-2 -mx-6 px-6">
                {['최적', '최단시간', '최소환승', '최소도보'].map((filter, index) => (
                    <button
                        key={filter}
                        className={`px-6 py-2 rounded-full border text-sm font-medium whitespace-nowrap active:scale-95 transition-all ${
                            index === 0 ? 'bg-secondary text-white border-transparent shadow-sm' : 'bg-white border-border text-foreground'
                        }`}
                    >
                        {filter}
                    </button>
                ))}
            </div>

            {/* 결과 리스트 */}
            <div className="flex flex-col gap-5 mt-2">
                <div className="flex justify-between items-center px-1">
                    <p className="text-sm font-bold text-foreground">총 {results.length}개의 경로</p>
                </div>

                {results.length > 0 ? (
                    results.map((path, index) => (
                        <PathResultCard key={index} path={path} />
                    ))
                ) : (
                    <div className="py-20 text-center text-muted-foreground">검색 결과가 없습니다.</div>
                )}
            </div>
        </div>
    );
}
