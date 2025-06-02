import {ProfileInterface} from "../../types/ProfileInterface";
import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";


export const requestUpdateProfile = async (profileData: Partial<ProfileInterface>): Promise<ProfileInterface> => {
    try {
        const response = await api.put('/profile', profileData, { withCredentials: true });
        return response.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};