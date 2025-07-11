import axios from "axios";
import {getDeviceType} from "../../utils/DeviceType";
import * as SecureStore from 'expo-secure-store';
import {apiUrl} from "../fetchWelcome";
import ErrorHandler from "../../app/(public)/error";


const api = axios.create({
    baseURL: apiUrl,
    withCredentials: true,
});

// Add device header to all services
api.interceptors.request.use(async (config) => {
    config.headers['X-Device'] = getDeviceType();
    config.headers['Content-Type'] = 'application/json';
    if (getDeviceType() !== 'WEB') {
        const token = await SecureStore.getItemAsync('authToken');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
    }

    return config;
});

api.interceptors.response.use(
    response => response,
    async (error) => {
        const originalRequest = error.config;

        // Only handle 401 errors and avoid infinite loops
        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                if (getDeviceType() === 'WEB') {
                    // For web, cookies are sent automatically with withCredentials: true
                    await api.post('/auth/refresh');
                } else {
                    // For mobile, manually handle refresh token
                    const refreshToken = await SecureStore.getItemAsync('refreshToken');
                    if (!refreshToken) {
                        ErrorHandler({
                            errorStatus: 401,
                            errorMessage: 'No refresh token available. Please log in again.'
                        });
                    }

                    const response = await api.post('/auth/refresh', {}, {
                        headers: {
                            'Authorization': `Bearer ${refreshToken}`
                        }
                    });

                    // Store new tokens
                    await SecureStore.setItemAsync('authToken', response.data.accessToken);
                    await SecureStore.setItemAsync('refreshToken', response.data.refreshToken);

                    // Update the original request with new token
                    originalRequest.headers['Authorization'] = `Bearer ${response.data.accessToken}`;
                }

                // Retry the original request
                return api(originalRequest);
            } catch (refreshError) {
                console.log(refreshError);
                // Refresh failed - clear tokens and redirect to login
                await SecureStore.deleteItemAsync('authToken');
                await SecureStore.deleteItemAsync('refreshToken');
                // Redirect to login or show login modal
                window.location.href = '/login'; // Or your navigation method
                return Promise.reject(refreshError);
            }
        }

        return Promise.reject(error);
    }
);

export default api;