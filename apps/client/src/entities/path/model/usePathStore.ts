import { create } from 'zustand';
import { type PathResult } from './types';

interface PathState {
    selectedPath: PathResult | null;
    setSelectedPath: (path: PathResult | null) => void;
}

/**
 * 경로 탐색 결과 중 선택된 특정 경로 데이터를 임시 저장하는 스토어
 * (상세 페이지에서 API 요청 시 객체 정보를 통째로 보내야 하므로 필요)
 */
export const usePathStore = create<PathState>((set) => ({
    selectedPath: null,
    setSelectedPath: (path) => set({ selectedPath: path }),
}));
