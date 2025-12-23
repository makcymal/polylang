import axios, { type AxiosInstance, type AxiosRequestConfig } from 'axios';

function getBaseUrl(): string {
    return 'http://localhost:3232/';
}

const config: AxiosRequestConfig = {
    baseURL: getBaseUrl(),
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
};

const instance: AxiosInstance = axios.create(config);

export default instance;
