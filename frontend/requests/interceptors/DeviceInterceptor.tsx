import axios from "axios";
import {getDeviceType} from "../../Utils/DeviceType";
import * as SecureStore from 'expo-secure-store';
import {apiUrl} from "../WelcomeRequest";


const api = axios.create({
    baseURL: apiUrl,
    withCredentials: true,
});

// Add device header to all requests
api.interceptors.request.use(async (config) => {
    config.headers['X-Device'] = getDeviceType();
    config.headers['Content-Type'] = 'application/json';
    // Add auth token for mobile
    if (getDeviceType() !== 'WEB') {
        const token = await SecureStore.getItemAsync('authToken');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
    }

    return config;
});

export default api;