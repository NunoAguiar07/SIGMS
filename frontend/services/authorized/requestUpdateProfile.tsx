import {ProfileInterface} from "../../types/ProfileInterface";
import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";


export const requestUpdateProfile = async (profileData: Partial<ProfileInterface>): Promise<ProfileInterface> => {
    try {
        console.log("Updating teacher with data:", profileData);
        const response = await api.put('/profile/update', profileData, { withCredentials: true });
        return response.data;
    } catch (error) {
        console.error("Error updating teacher:", error);
        throw handleAxiosError(error);
    }
};