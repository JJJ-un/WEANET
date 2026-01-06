import { createFileRoute } from "@tanstack/react-router";
import PathsSearchHeader from "@/widget/path-search-header/ui/PathSearchHeader";

export const Route = createFileRoute("/path-search/")({
  component: PathSearchPage,
});

function PathSearchPage() {
  return (
    <div>
      {/* 헤더 컴포넌트 */}
      <div>
        <PathsSearchHeader />
      </div>
      {/* 검색 바 및 최근 검색어 컴포넌트 */}
      <div>
        <div>검색 바 컴포넌트</div>
      </div>
      {/* 최근 검색어 컴포넌트 */}
      <div>
        <div>최근 검색어 컴포넌트</div>
      </div>
    </div>
  );
}
