import { create } from 'zustand';

interface WeatherState {
    selectedLocation: string;
    setSelectedLocation: (location: string) => void;
}

/**
 * 선택된 지역 상태를 관리하는 전역 스토어
 */
export const useWeatherStore = create<WeatherState>((set) => ({
    selectedLocation: '강남구',
    setSelectedLocation: (location) => set({ selectedLocation: location }),
}));
