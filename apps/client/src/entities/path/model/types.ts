export type TransportType = 'subway' | 'bus' | 'walk';

export interface PathStep {
    type: TransportType;
    duration: number; // 분 단위
    lineName?: string; // 노선명 (ex: '2호선', '9401번')
}

export interface PathResult {
    id: string;
    totalDuration: number; // 총 소요 시간 (분)
    fare?: number; // 요금 (원)
    transferCount: number; // 환승 횟수
    steps: PathStep[];
    weatherTip?: string; // 날씨 기반 추천 (ex: '중간에 비가 올 수 있으니 우산을 챙기세요')
    isRecommended?: boolean; // 최적 경로 여부
}
