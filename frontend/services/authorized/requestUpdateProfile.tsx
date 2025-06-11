import {ProfileInterface} from "../../types/ProfileInterface";
import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";


export const requestUpdateProfile = async (profileData: Partial<ProfileInterface>): Promise<ProfileInterface> => {
    try {
        console.log("Updating profile with data:", profileData);
        const response = await api.put('/profile', profileData, { withCredentials: true });
        return response.data;
    } catch (error) {
        console.error("Error updating profile:", error);
        throw handleAxiosError(error);
    }
};