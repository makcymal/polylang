import axios, { type AxiosInstance, type AxiosRequestConfig } from 'axios';

const BASE_URL = 'localhost:3232/';

const axiosConfig: AxiosRequestConfig = {
    baseURL: 'http://' + BASE_URL,
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    },
    withCredentials: true,
};

let _http: AxiosInstance | null = null;

export const http = (): AxiosInstance => {
    _http ??= axios.create(axiosConfig);
    return _http;
}

let _ws: WebSocket | null = null;

export const ws = (): WebSocket => {
    _ws ??= new WebSocket('ws://' + BASE_URL);
    return _ws;
}
