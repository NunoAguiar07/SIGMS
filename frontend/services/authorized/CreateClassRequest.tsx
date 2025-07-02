import {handleAxiosError} from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";

/**
 * Creates a new class under a specified subject.
 * @param {string} className - The name of the class to be created.
 * @param {number} subjectId - The ID of the subject under which the class is created.
 * @returns {Promise<boolean>} - Returns true if the class was created successfully, otherwise throws an error.
 */
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