import { useState, useMemo } from "react";
import { Input } from "@/shared/ui/input";
import DestinationIcon from "@/shared/assets/icons/destination.svg?react";
import DepartureIcon from "@/shared/assets/icons/departure.svg?react";
import SEARCH_LIST_MOCK from "@/shared/mock/searchData.mock";

// 1. 타입 정의: 필드 확장에 유연하게 대응
type SearchField = "departure" | "destination" | null;

interface PathState {
  departure: string;
  destination: string;
}

const PathsSearchBar = () => {
  // 2. 상태 통합: 출발지와 도착지를 하나의 객체로 관리
  const [paths, setPaths] = useState<PathState>({
    departure: "",
    destination: "",
  });

  const [activeField, setActiveField] = useState<SearchField>(null);

  // 3. 파생 상태: 현재 입력 중인 값 계산
  const activeValue = activeField ? paths[activeField] : "";

  // 4. 연산 최적화: useMemo를 사용하여 불필요한 필터링 방지
  const filteredSearchList = useMemo(() => {
    const keyword = activeValue.trim();
    if (!keyword) return [];

    return SEARCH_LIST_MOCK.filter((item) =>
      item.name.toLowerCase().includes(keyword.toLowerCase())
    );
  }, [activeValue]);

  // 5. 조건부 렌더링 논리
  const isShowList = activeField !== null && activeValue.trim().length > 0;

  // 6. 핸들러 통합: 동적 키([])를 사용하여 if문 제거
  const handleInputChange = (field: "departure" | "destination", value: string) => {
    setPaths((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleSelect = (name: string) => {
    if (activeField) {
      handleInputChange(activeField, name);
    }
    setActiveField(null);
  };

  return (
    <div className="flex flex-col gap-4 p-4 bg-background border-2 rounded-[10px] relative w-full max-w-md">
      {/* 출발지 섹션 */}
      <div className="flex items-center gap-3">
        <DepartureIcon className="shrink-0" />
        <div className="flex-1">
          <p className="text-xs text-gray-500 mb-1">출발지</p>
          <Input
            value={paths.departure}
            onChange={(e) => handleInputChange("departure", e.target.value)}
            onFocus={() => setActiveField("departure")}
            onBlur={() => setTimeout(() => setActiveField(null), 200)}
            placeholder="출발지를 입력하세요"
            className="w-full"
          />
        </div>
      </div>

      {/* 도착지 섹션 */}
      <div className="flex items-center gap-3">
        <DestinationIcon className="shrink-0" />
        <div className="flex-1">
          <p className="text-xs text-gray-500 mb-1">도착지</p>
          <Input
            value={paths.destination}
            onChange={(e) => handleInputChange("destination", e.target.value)}
            onFocus={() => setActiveField("destination")}
            onBlur={() => setTimeout(() => setActiveField(null), 200)}
            placeholder="도착지를 입력하세요"
            className="w-full"
          />
        </div>
      </div>

      {/* 검색 결과 목록 */}
      {isShowList && (
        <div className="absolute top-[calc(100%+8px)] left-0 w-full max-h-52 bg-white border shadow-xl rounded-xl z-50 overflow-y-auto">
          {filteredSearchList.length > 0 ? (
            filteredSearchList.map((item) => (
              <div
                key={item.id}
                onMouseDown={() => handleSelect(item.name)}
                className="p-3 border-b last:border-0 hover:bg-slate-50 cursor-pointer transition-colors"
              >
                <p className="font-semibold text-sm text-slate-800">{item.name}</p>
                <p className="text-xs text-slate-500 mt-0.5">{item.address}</p>
              </div>
            ))
          ) : (
            <div className="p-8 text-center">
              <p className="text-sm text-slate-400">검색 결과가 없습니다.</p>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default PathsSearchBar;