import { useRouter } from "@tanstack/react-router";
import BackArrow from "@/shared/assets/icons/back-arrow.svg?react";

const PathsSearchHeader = () => {
  const router = useRouter();

  const handleBack = () => {
    // 이전 페이지로 이동
    router.history.back();
  };

  return (
    <div className="flex items-center gap-4 p-4 bg-background">
      <button
        type="button"
        onClick={handleBack}
        aria-label="이전 페이지로 이동"
        className="text-muted-foreground"
      >
        <BackArrow />
      </button>
      <h2 className="text-base">경로 검색</h2>
    </div>
  );
};

export default PathsSearchHeader;
