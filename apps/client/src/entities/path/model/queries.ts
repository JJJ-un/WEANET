import { useState, useEffect } from 'react';
import { MOCK_PATH_RESULTS } from '@/shared/mock/pathData.mock';

/**
 * 경로 검색 로직을 담당하는 커스텀 훅 (디자인 작업을 위해 Mock 데이터 사용)
 */
export const usePathSearch = () => {
    const [searchParams, setSearchParams] = useState<{ departure: string; destination: string } | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [mockResults, setMockResults] = useState<typeof MOCK_PATH_RESULTS>([]);

    useEffect(() => {
        if (searchParams) {
            setIsLoading(true);
            const timer = setTimeout(() => {
                setMockResults(MOCK_PATH_RESULTS);
                setIsLoading(false);
            }, 1000);
            return () => clearTimeout(timer);
        }
    }, [searchParams]);

    const handleSearch = (paths: { departure: string; destination: string }) => {
        setSearchParams(paths);
    };

    const clearSearch = () => {
        setSearchParams(null);
        setMockResults([]);
    };

    return {
        results: mockResults,
        isLoading,
        isError: false,
        handleSearch,
        clearSearch,
        showResults: !!searchParams,
    };
};
