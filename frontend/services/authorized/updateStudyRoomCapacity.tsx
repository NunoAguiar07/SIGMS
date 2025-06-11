import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";


export const updateStudyRoomCapacity = async (): Promise<void> => {
    try {
        await api.put('/devices/update', { withCredentials: true });
    } catch (error) {
        throw handleAxiosError(error);
    }
};