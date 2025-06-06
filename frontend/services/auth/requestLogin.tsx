import axios from 'axios';
import * as SecureStore from 'expo-secure-store';
import {handleAxiosError} from "../../utils/HandleAxiosError";
import {apiUrl} from "../fetchWelcome";
import {ParsedError} from "../../types/errors/ParseErrorTypes";

export const requestLogin = async (
    email: string,
    password: string,
    deviceType: string
): Promise<{ success: boolean; error?: ParsedError }> => {
    try {
        const response = await axios.post(
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
            const token = response.data.token;
            if (deviceType !== 'WEB' && token) {
                await SecureStore.setItemAsync('authToken', token);
            }
            return { success: true };
        }
        return { success: false };
    } catch (error) {
        throw handleAxiosError(error);
    }
};