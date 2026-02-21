import { createFileRoute } from '@tanstack/react-router';
import PathsSearchHeader from '@/widget/path-search-header/ui/PathSearchHeader';
import PathsSearchBar from '@/widget/path-search/ui/PathSearchBar';
import RecentSearch from '@/widget/recent-search/ui/RecentSearch';

export const Route = createFileRoute('/path-search/')({
    component: PathSearchPage,
});

function PathSearchPage() {
    const handleSelectRecent = (name: string) => {
        console.log('최근 검색어 선택:', name);
        // TODO: 검색바의 입력값으로 설정하거나 바로 검색 실행 로직 추가
    };

    return (
        <div className="flex flex-col gap-8 pb-32">
            {/* 1. 헤더 섹션 (뒤로가기 등) */}
            <PathsSearchHeader />

            {/* 2. 경로 탐색 바 위젯 */}
            <PathsSearchBar />

            {/* 3. 최근 검색어 섹션 (위젯 분리 완료) */}
            <RecentSearch onSelect={handleSelectRecent} maxItems={3} />
        </div>
    );
}
