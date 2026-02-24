import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { searchRoutes } from '../api/pathApi';

/**
 * 경로 검색 로직을 담당하는 커스텀 훅
 */
export const usePathSearch = () => {
    const [searchParams, setSearchParams] = useState<{ departure: string; destination: string } | null>(null);

    const { data: results, isLoading, isError } = useQuery({
        queryKey: ['pathSearch', searchParams?.departure, searchParams?.destination],
        queryFn: () =>
            searchRoutes({
                departureName: searchParams!.departure,
                destinationName: searchParams!.destination,
            }),
        enabled: !!searchParams,
    });

    const handleSearch = (paths: { departure: string; destination: string }) => {
        setSearchParams(paths);
    };

    const clearSearch = () => {
        setSearchParams(null);
    };

    return {
        results: results || [],
        isLoading,
        isError,
        handleSearch,
        clearSearch,
        showResults: !!searchParams,
    };
};
