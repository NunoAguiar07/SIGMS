import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";

export const leaveClass = async (subjectId: number, classId: number): Promise<boolean> => {
    try {
        const response = await api.delete(
            `/subjects/${encodeURIComponent(subjectId)}/classes/${encodeURIComponent(classId)}/users/remove`,
            { withCredentials: true }
        );
        return response.status === 204;
    } catch (error) {
        throw handleAxiosError(error);
    }
};