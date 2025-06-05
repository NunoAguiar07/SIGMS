import {handleAxiosError} from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";


export const createSubject = async (subjectName: string): Promise<boolean> => {
    try {
        const response = await api.post('/subjects/add',
            { name: subjectName },
            { withCredentials: true }
        );
        return response.status === 201;
    } catch (error) {
        throw handleAxiosError(error);
    }
};