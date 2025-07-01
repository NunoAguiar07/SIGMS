import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";

export const fetchTeacherProfileById = async (teacherId: number): Promise<any> => {
    try {
        const response = await api.get(`/profile/${encodeURIComponent(teacherId)}`, { withCredentials: true });
        console.log("Fetched teacher profile data:", response.data);
        return response.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
}