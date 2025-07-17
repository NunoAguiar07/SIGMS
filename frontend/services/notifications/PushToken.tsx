import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import * as Notifications from "expo-notifications";

export const pushToken = async () => {
    const tokenData = await Notifications.getExpoPushTokenAsync({
        projectId: '5d959d54-1140-4d2d-a04c-7df400932c60',
    })
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