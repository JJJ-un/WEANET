export type TransportType = 'SUBWAY' | 'BUS' | 'WALK';

export interface PathStep {
    sequence: number;
    transportType: TransportType;
    lineName: string;
    lineId?: string | null;
    startStationName: string;
    startStationId?: string | null;
    endStationName: string;
    endStationId?: string | null;
    lat?: number;
    lng?: number;
    sectionTime: number; // 해당 단계의 소요 시간 (분)
}

export type PathLabel = '최적' | '최소환승' | '최단시간' | '최소도보' | '추천';

/**
 * 경로 검색 결과 요약 인터페이스 (검색 결과 리스트용)
 */
export interface PathResult {
    totalTime: number; // 총 소요 시간 (분)
    totalFare: number; // 요금
    transferCount: number; // 환승 횟수
    summary: string; // 예: "수도권4호선 -> 수도권2호선"
    steps: PathStep[];
    // UI 전용 확장 필드
    id?: string;
    arrivalTime?: string;
    walkDuration?: number;
    labels?: PathLabel[];
    weatherTip?: string;
}
