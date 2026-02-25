import { type WeatherStatus } from '@/entities/weather/model/types';

export type TransportType = 'SUBWAY' | 'BUS' | 'WALK';

/**
 * 단계별 날씨 정보
 */
export interface StepWeather {
    weather: WeatherStatus;
    currentTemp: number;
    maxTemp: number;
    minTemp: number;
    precipitationProbability: number;
    advice: string;
}

/**
 * 경로의 각 단계 기본 정보 (검색 결과 목록용)
 */
export interface PathStep {
    sequence: number;
    transportType: TransportType;
    lineName: string;
    lineId: string | null;
    startStationName: string;
    startStationId: string | null;
    endStationName: string;
    endStationId: string | null;
    lat: number;
    lng: number;
    sectionTime: number; // 해당 단계 소요 시간 (분)
    arrivalMessage: string | null;
}

/**
 * 상세 경로의 각 단계 정보 (날씨 및 혼잡도 포함)
 */
export interface DetailPathStep extends PathStep {
    weather: StepWeather;
    congestion: string | null;
}

export type PathLabel = '최적' | '최소환승' | '최단시간' | '최소도보' | '추천';

export type CongestionStatus = 'relaxed' | 'normal' | 'busy';

/**
 * 전체 경로 검색 결과 (목록용)
 */
export interface PathResult {
    totalTime: number;
    totalFare: number;
    transferCount: number;
    summary: string;
    integratedAdvice: string;
    steps: PathStep[];
}

/**
 * 전체 경로 상세 결과 (상세보기용)
 */
export interface DetailPathResult {
    totalTime: number;
    totalFare: number;
    transferCount: number;
    summary: string;
    integratedAdvice: string;
    steps: DetailPathStep[];
}
