import axios, { AxiosRequestConfig, AxiosResponse } from "axios";

import { apiCache } from "../../utils/cache";
import { ApiErrorResponse } from "@/types/reponse/ApiErrorResponse";
import { ApiSuccessResponse } from "@/types/reponse/ApiSuccessResponse";

const apiClient = axios.create({
    baseURL: process.env.REACT_APP_TRANSACTION_URL,
    timeout: 10000,
    headers: {
        "Content-Type": "application/json"
    },
});

apiClient.interceptors.request.use((config) => {
    if(typeof window !== "undefined") {
        const token = localStorage.getItem("authToken");
        if(token) {
            config.headers["Authorization"] = `Bearer ${token}`;
        }
    }

    if(config.method?.toUpperCase() === 'GET') {
        const cached = apiCache.get(config);
        if(cached) {
            return Promise.reject({ __isCached: true, cachedResponse: cached });
        }
    }

    return config;
});

apiClient.interceptors.response.use(
    (response) => {
        const method = response.config.method?.toUpperCase() || '';
        if(method === 'GET') {
            apiCache.set(response.config, response);
        } 
        else if(['POST', 'PUT', 'DELETE'].includes(method)) {
            apiCache.clear();
        }
        return response;
    },
    (error) => {
        if(error.__isCached && error.cachedResponse) {
            return Promise.resolve(error.cachedResponse);
        }

        if(axios.isAxiosError(error) && error.response) {
            const status = error.response.status;
            if((status === 401 || status === 403) && error.config && !error.config.url?.includes("/login")) {
                localStorage.removeItem("authToken");
                window.location.href = "/login";
            }
        }

        return Promise.reject(error);
    }
);

export async function apiRequest<T>(config: AxiosRequestConfig): Promise<ApiSuccessResponse<T> | ApiErrorResponse> {
    try {
        const response: AxiosResponse<T> = await apiClient.request<T>(config);
        return {
            success: true,
            data: response.data,
            status: response.status,
            statusText: response.statusText,
        }
    }
    catch(error) {
        if(axios.isAxiosError(error)) {
            return {
                success: false,
                message: error.response?.data?.message || error.message || "API request failed",
                status: error.response?.status || 500,
                error: error.response?.data || error,
                timestamp: new Date().toISOString(),
            }
        }

        return {
            success: false,
            message: "Network or unexpected error",
            status: 500,
            error: error,
            timestamp: new Date().toISOString(),
        }
    }
};

export function isApiSuccessResponse<T>(response: ApiSuccessResponse<T> | ApiErrorResponse): response is ApiSuccessResponse<T> {
    return (response as ApiSuccessResponse<T>).data !== undefined;
};