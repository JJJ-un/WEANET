import { apiClient } from '@/shared/api/base';
import { type PathResult, type DetailPathResult } from '../model/types';

/**
 * 경로 검색 요청 파라미터
 */
export interface PathSearchParams {
    departureName: string;
    destinationName: string;
}

/**
 * 경로 검색 API (POST /routes/search)
 * @param params { departureName, destinationName }
 */
export const searchRoutes = async (params: PathSearchParams): Promise<PathResult[]> => {
    const { data } = await apiClient.post<PathResult[]>(`/routes/search`, params);
    return data;
};

/**
 * 경로 상세 정보 API (POST /routes/details)
 * @param path 선택된 경로 객체
 */
export const getRouteDetails = async (path: PathResult): Promise<DetailPathResult> => {
    const { data } = await apiClient.post<DetailPathResult>(`/routes/details`, path);
    return data;
};
