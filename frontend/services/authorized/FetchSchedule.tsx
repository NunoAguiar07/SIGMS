import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import {ScheduleApiResponse} from "../../types/ScheduleApiResponse";


export const fetchSchedule = async (): Promise<ScheduleApiResponse> => {
    try {
        const response = await api.get('/schedule', { withCredentials: true });
        return response.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};