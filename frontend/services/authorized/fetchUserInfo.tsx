import api from "../interceptors/DeviceInterceptor";
import AsyncStorage from "@react-native-async-storage/async-storage";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import {UserInfo} from "../../types/auth/authTypes";


export const fetchUserInfo = async (): Promise<UserInfo> => {
    try {
        const response = await api.get('userInfo', { withCredentials: true });
        const { userId, universityId, userRole } = response.data;
        await AsyncStorage.multiSet([
            ['userId', JSON.stringify(userId)],
            ['universityId', JSON.stringify(universityId)],
            ['userRole', userRole]
        ]);
        return response.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};