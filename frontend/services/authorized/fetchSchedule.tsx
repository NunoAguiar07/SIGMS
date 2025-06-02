import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";
import {Schedule} from "../../types/calendar/Schedule";


export const fetchSchedule = async (): Promise<Schedule> => {
    try {
        const response = await api.get('/schedule', { withCredentials: true });
        return response.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};