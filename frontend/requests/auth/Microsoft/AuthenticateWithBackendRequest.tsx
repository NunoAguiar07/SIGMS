import axios from "axios";
import {getDeviceType} from "../../../Utils/DeviceType";
import * as SecureStore from 'expo-secure-store';
import {apiUrl} from "../../WelcomeRequest";

export const authenticateWithBackend = async (accessToken: string) => {
    try {
        const response = await axios.post(
            `${apiUrl}auth/microsoft`,
            {},
            {
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${accessToken}`,
                    'X-Device': getDeviceType()
                }
            }
        );

        if (getDeviceType() !== 'WEB' && response.data.token) {
            await SecureStore.setItemAsync('authToken', response.data.token);
        }

        return response.data;
    } catch (error) {
        console.error('Backend authentication failed:', error);
        throw error;
    }
};