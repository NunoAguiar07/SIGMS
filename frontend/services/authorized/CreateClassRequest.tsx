import {handleAxiosError} from "../../Utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";


export const createClass = async (className: string, subjectId: number): Promise<boolean> => {
    try {
        const response = await api.post(
            `subjects/${encodeURIComponent(subjectId)}/classes/add`,
            { name: className },
            { withCredentials: true }
        );
        return response.status === 201;
    } catch (error) {
        throw handleAxiosError(error);
    }
};