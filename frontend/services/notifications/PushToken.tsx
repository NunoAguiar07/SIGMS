import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import * as Notifications from "expo-notifications";

export const pushToken = async () => {
    const tokenData = await Notifications.getExpoPushTokenAsync()
    try {
        const response = await api.post(
            `expoToken`,
            {},
            {
                headers: {
                    'X-Expo-Token': tokenData.data,
                },
                withCredentials: true
            }
        );
        return response.status === 201;
    } catch (error) {
        throw handleAxiosError(error);
    }
}