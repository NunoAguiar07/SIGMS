import axios from 'axios';
import * as SecureStore from 'expo-secure-store';
import {handleAxiosError} from "../../Utils/HandleAxiosError";

export const LoginRequest = (email: string, password:string, deviceType:string, setError: any) => {
    return async () => {
        const apiUrl = process.env.EXPO_PUBLIC_API_URL
        try {
            const response = await axios.post(
                `${apiUrl}/auth/login`,
                {email, password},
                {
                    headers: {
                        'Content-Type': 'application/json',
                        'X-Device': deviceType,
                    },
                    withCredentials: true,
                }
            );
            if(response.status === 200) {
                const token = response.data.token;
                if (deviceType !== 'WEB' && token) {
                    await SecureStore.setItemAsync('authToken', token);
                }
            }
            return true
        } catch (error) {
            handleAxiosError(error, setError)
        }
    }
};