import {handleAxiosError} from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";


/**
 * Creates a new subject with the specified name.
 * @param {string} subjectName - The name of the subject to be created.
 * @returns {Promise<boolean>} - Returns true if the subject was created successfully, otherwise throws an error.
 */
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