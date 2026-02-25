import { CongestionStatus } from '../model/types';

/**
 * 혼잡도 문자열을 상태 키워드로 변환합니다.
 */
export const getCongestionStatus = (congestion?: string | null): CongestionStatus | null => {
    if (congestion?.includes('여유')) return 'relaxed';
    if (congestion?.includes('보통')) return 'normal';
    if (congestion?.includes('혼잡')) return 'busy';
    return null;
};

/**
 * 상태별 스타일 및 게이지 단계 설정
 */
export const CONGESTION_STYLE = {
    relaxed: {
        text: 'text-relaxed',
        bg: 'bg-relaxed',
        fillLevel: 1,
    },
    normal: {
        text: 'text-normal',
        bg: 'bg-normal',
        fillLevel: 2,
    },
    busy: {
        text: 'text-busy',
        bg: 'bg-busy',
        fillLevel: 3,
    },
    default: {
        text: 'text-muted-foreground',
        bg: 'bg-muted',
        fillLevel: 0,
    },
} as const;
