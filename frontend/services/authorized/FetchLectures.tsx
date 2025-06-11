import api from "../interceptors/DeviceInterceptor";
import {Lecture} from "../../types/calendar/Lecture";
import {handleAxiosError} from "../../utils/HandleAxiosError";

export const fetchLectures = async () : Promise<Lecture[]> => {
    try {
        const response = await api.get(`/lectures`, { withCredentials: true });
        console.log("Lectures fetched successfully:", response.data.data);
        return response.data.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};