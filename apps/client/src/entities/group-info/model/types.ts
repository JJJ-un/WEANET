export type InfoCategory = 'subway' | 'bus' | 'path-weather' | 'user-report';

export interface GroupInfoItem {
    id: string;
    category: InfoCategory;
    content: string;
    participants?: number; // 제보 데이터에만 있을 수 있음
    isUrgent?: boolean;
}

// 각 API 응답을 시뮬레이션하기 위한 인터페이스들
export interface TransportData extends GroupInfoItem {
    line?: string; // 노선 정보 (ex: '2호선', '9401번')
}

export interface PathWeatherData extends GroupInfoItem {
    location?: string; // 특정 지점 (ex: '양재역 인근')
}
