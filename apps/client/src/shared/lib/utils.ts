import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

/**
 * 현재 시간을 기준으로 소요 시간을 더해 도착 시간을 반환합니다. (HH:mm 형식)
 * @param duration 소요 시간 (분)
 */
export function getArrivalTime(duration: number): string {
    const now = new Date();
    const arrival = new Date(now.getTime() + duration * 60000);
    return `${arrival.getHours().toString().padStart(2, '0')}:${arrival.getMinutes().toString().padStart(2, '0')}`;
}

/**
 * 전체 값 대비 현재 값의 백분율(%)을 계산하여 문자열로 반환합니다.
 */
export function calculatePercentage(current: number, total: number): string {
    const percentage = total > 0 ? (current / total) * 100 : 0;
    return `${percentage}%`;
}
