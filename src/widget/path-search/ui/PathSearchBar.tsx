import { Input } from "@/shared/ui/input";
import { useState } from "react";
import DestinationIcon from "@/shared/assets/icons/destination.svg?react";
import DepartureIcon from "@/shared/assets/icons/departure.svg?react";

const PathsSearchBar = () => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="flex flex-col items-center gap-4 p-4 bg-background border-2 rounded-[10px]">
      <div className="flex items-center gap-2">
        <DepartureIcon />
        <div>
          <p className="text-sm">출발지</p>
          <Input
            onFocus={() => setIsOpen(true)}
            onBlur={() => setIsOpen(false)}
          />
        </div>
      </div>
      <div className="flex items-center gap-2">
        <DestinationIcon />
        <div>
          <p>도착지</p>
          <Input
            placeholder="도착위치를 입력하세요"
            onFocus={() => setIsOpen(true)}
            onBlur={() => setIsOpen(false)}
          />
        </div>
      </div>
      <div>
        {isOpen && (
          <div className="w-full h-40 bg-white border mt-2 rounded-lg">
            검색 결과 영역
          </div>
        )}
      </div>
    </div>
  );
};

export default PathsSearchBar;
