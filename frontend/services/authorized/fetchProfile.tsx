import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import {ProfileInterface} from "../../types/ProfileInterface";

export const fetchProfile = async (): Promise<ProfileInterface> => {
    try {
        const response = await api.get('/profile', { withCredentials: true });
        console.log("Fetched teacher data:", response.data);
        return response.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};