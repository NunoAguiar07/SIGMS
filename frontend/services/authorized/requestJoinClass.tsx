import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";


export const joinClass = async (subjectId: number, classId: number): Promise<boolean> => {
    try {
        const response = await api.post(
            `/subjects/${encodeURIComponent(subjectId)}/classes/${encodeURIComponent(classId)}/users/add`,
            {},
            { withCredentials: true }
        );
        return response.status === 204;
    } catch (error) {
        throw handleAxiosError(error);
    }
};