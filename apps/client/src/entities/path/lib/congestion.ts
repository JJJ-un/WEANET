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

/**
 * 지하철 노선 이름을 기반으로 브랜드 색상 클래스를 반환합니다.
 */
export const getLineColor = (lineName: string) => {
    if (lineName.includes('1호선')) return 'bg-[#0052A4]';
    if (lineName.includes('2호선')) return 'bg-[#00A84D]';
    if (lineName.includes('3호선')) return 'bg-[#EF7C1C]';
    if (lineName.includes('4호선')) return 'bg-[#00A4E3]';
    if (lineName.includes('5호선')) return 'bg-[#996CAC]';
    if (lineName.includes('6호선')) return 'bg-[#CD7C2F]';
    if (lineName.includes('7호선')) return 'bg-[#747F00]';
    if (lineName.includes('8호선')) return 'bg-[#E6186C]';
    if (lineName.includes('9호선')) return 'bg-[#BDB092]';
    if (lineName.includes('수인분당')) return 'bg-[#FABE00]';
    if (lineName.includes('신분당')) return 'bg-[#D4003B]';
    if (lineName.includes('경의중앙')) return 'bg-[#77C4A3]';
    if (lineName.includes('경춘')) return 'bg-[#178C72]';
    if (lineName.includes('공항')) return 'bg-[#0090D2]';
    return 'bg-primary';
};
