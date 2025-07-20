import * as SecureStore from 'expo-secure-store';
import {handleAxiosError} from "../../utils/HandleAxiosError";
import {apiUrl} from "../fetchWelcome"
import api from "../interceptors/DeviceInterceptor";
import {pushToken} from "../notifications/PushToken";

export const requestLogin = async (
    email: string,
    password: string,
    deviceType: string
): Promise<boolean> => {
    try {
        const response = await api.post(
            `${apiUrl}auth/login`,
            { email, password },
            {
                headers: {
                    'Content-Type': 'application/json',
                    'X-Device': deviceType,
                },
                withCredentials: true,
            }
        );
        if (response.status === 200) {
            if (deviceType !== 'WEB') {
                const {accessToken, refreshToken} = response.data;
                await SecureStore.setItemAsync('authToken', accessToken);
                await SecureStore.setItemAsync('refreshToken', refreshToken);
                await pushToken()
            }
            return true;
        }
        return false
    } catch (error) {
        throw handleAxiosError(error)
    }
};