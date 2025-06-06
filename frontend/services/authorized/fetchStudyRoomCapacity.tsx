
import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";
import {StudyRoomCapacity} from "../../types/StudyRoomCapacity";

export const fetchStudyRoomCapacity = async (): Promise<StudyRoomCapacity[]> => {
    try {
        const response = await api.get('/devices', { withCredentials: true });
        return response.data.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};