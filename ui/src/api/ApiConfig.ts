import axios, {type AxiosError, type AxiosInstance, type AxiosRequestConfig} from 'axios';

const BASE_URL = 'localhost:3232/';

const axiosConfig: AxiosRequestConfig = {
    baseURL: 'http://' + BASE_URL,
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    },
    withCredentials: true,
};

export const apiClient: AxiosInstance = axios.create(axiosConfig);

// let isRefreshing = false;

// apiClient.interceptors.response.use(
//     response => response,
//     async (error: AxiosError) => {
//         const originalRequest = error.config as AxiosRequestConfig & {
//             _retry?: boolean;
//         };
//
//         if (
//             error.response?.status === 401 &&
//             !originalRequest._retry &&
//             !originalRequest.url?.includes('/users/login')
//         ) {
//             originalRequest._retry = true;
//
//             try {
//                 if (!isRefreshing) {
//                     isRefreshing = true;
//                     await apiClient.post('/users/refresh');
//                     isRefreshing = false;
//                 }
//
//                 return apiClient(originalRequest);
//             } catch (refreshError) {
//                 isRefreshing = false;
//                 return Promise.reject(refreshError);
//             }
//         }
//
//         return Promise.reject(error);
//     }
// );
