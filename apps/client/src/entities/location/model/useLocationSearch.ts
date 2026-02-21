import { useMemo } from 'react';
import SEARCH_LIST_MOCK from '@/shared/mock/searchData.mock';

/**
 * 장소(Location) 기본 인터페이스
 */
export interface LocationItem {
    id: number | string;
    name: string;
    address: string;
}

/**
 * 장소 검색을 담당하는 커스텀 훅
 * @param keyword 검색어
 * @param limit 결과 개수 제한 (기본값 5)
 */
export const useLocationSearch = (keyword: string, limit: number = 5) => {
    const results = useMemo(() => {
        const trimmedKeyword = keyword.trim().toLowerCase();
        
        if (!trimmedKeyword) return [];

        return SEARCH_LIST_MOCK
            .filter((item) => 
                item.name.toLowerCase().includes(trimmedKeyword) || 
                item.address.toLowerCase().includes(trimmedKeyword)
            )
            .slice(0, limit);
    }, [keyword, limit]);

    return {
        results,
        isEmpty: keyword.trim().length > 0 && results.length === 0,
    };
};
