
import api from "../interceptors/DeviceInterceptor";
import {StudyRoomCapacity} from "../../types/StudyRoomCapacity";
import {handleAxiosError} from "../../utils/HandleAxiosError";

export const fetchStudyRoomCapacity = async (): Promise<StudyRoomCapacity[]> => {
    try {
        const response = await api.get('/devices', { withCredentials: true });
        return response.data.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};