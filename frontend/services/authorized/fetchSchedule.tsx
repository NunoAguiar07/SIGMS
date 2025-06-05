import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import {Lecture} from "../../types/calendar/Lecture";


export const fetchSchedule = async (): Promise<Lecture[]> => {
    try {
        const response = await api.get('/schedule', { withCredentials: true });
        return response.data.lectures;
    } catch (error) {
        throw handleAxiosError(error);
    }
};