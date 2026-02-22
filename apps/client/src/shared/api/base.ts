import axios from 'axios';

/**
 * 프로젝트 전체에서 사용할 공통 Axios 인스턴스
 */
export const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
});

// 인터셉터를 통해 에러 처리 등을 공통으로 관리할 수 있습니다.
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error('API Error:', error.response?.data || error.message);
        return Promise.reject(error);
    },
);
