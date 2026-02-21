export type TransportType = 'subway' | 'bus' | 'walk';

export interface PathStep {
    type: TransportType;
    duration: number; // 해당 단계의 소요 시간 (분)
    lineName?: string; // 노선명 (ex: '2호선', '9401번')
    color?: string; // 노선 색상 (선택 사항)
}

export type PathLabel = '최적' | '최소환승' | '최단시간' | '최소도보' | '추천';

export interface PathResult {
    id: string;
    totalDuration: number; // 총 소요 시간 (분)
    arrivalTime: string; // 도착 예정 시간 (ex: '14:25')
    fare: number; // 요금
    transferCount: number; // 환승 횟수
    walkDuration: number; // 총 도보 시간 (분)
    steps: PathStep[];
    labels: PathLabel[]; // 여러 라벨 가능 (ex: ['최적', '추천'])
    weatherTip?: string;
}
