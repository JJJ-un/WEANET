import type { CongestionStatus } from '../model/types';

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

/**
 * 지하철 노선 이름을 기반으로 디자인 시스템 스타일을 반환합니다.
 * Tailwind의 정적 분석을 위해 클래스명 전체를 문자열로 명시합니다.
 */
export const getSubwayStyle = (lineName: string) => {
    if (lineName.includes('1호선')) return { bg: 'bg-line-1', text: 'text-line-1', border: 'border-line-1' };
    if (lineName.includes('2호선')) return { bg: 'bg-line-2', text: 'text-line-2', border: 'border-line-2' };
    if (lineName.includes('3호선')) return { bg: 'bg-line-3', text: 'text-line-3', border: 'border-line-3' };
    if (lineName.includes('4호선')) return { bg: 'bg-line-4', text: 'text-line-4', border: 'border-line-4' };
    if (lineName.includes('5호선')) return { bg: 'bg-line-5', text: 'text-line-5', border: 'border-line-5' };
    if (lineName.includes('6호선')) return { bg: 'bg-line-6', text: 'text-line-6', border: 'border-line-6' };
    if (lineName.includes('7호선')) return { bg: 'bg-line-7', text: 'text-line-7', border: 'border-line-7' };
    if (lineName.includes('8호선')) return { bg: 'bg-line-8', text: 'text-line-8', border: 'border-line-8' };
    if (lineName.includes('9호선')) return { bg: 'bg-line-9', text: 'text-line-9', border: 'border-line-9' };
    if (lineName.includes('수인분당')) return { bg: 'bg-line-suin-bundang', text: 'text-line-suin-bundang', border: 'border-line-suin-bundang' };
    if (lineName.includes('신분당')) return { bg: 'bg-line-shin-bundang', text: 'text-line-shin-bundang', border: 'border-line-shin-bundang' };
    if (lineName.includes('경의중앙')) return { bg: 'bg-line-gyeongui-jungang', text: 'text-line-gyeongui-jungang', border: 'border-line-gyeongui-jungang' };
    if (lineName.includes('경춘')) return { bg: 'bg-line-gyeongchun', text: 'text-line-gyeongchun', border: 'border-line-gyeongchun' };
    if (lineName.includes('공항')) return { bg: 'bg-line-airport', text: 'text-line-airport', border: 'border-line-airport' };
    
    return { bg: 'bg-primary', text: 'text-primary', border: 'border-primary' };
};
