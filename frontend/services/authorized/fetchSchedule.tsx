import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import {Lecture} from "../../types/calendar/Lecture";
import {ScheduleApiResponse} from "../../types/ScheduleApiResponse";


export const fetchSchedule = async (): Promise<ScheduleApiResponse> => {
    try {
        const response = await api.get('/schedule', { withCredentials: true });
        console.log("Fetched schedule:", response.data);
        return response.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};