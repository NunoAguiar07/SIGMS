import {handleAxiosError} from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";


/**
 * Deletes a class with the specified ID under a subject.
 * @param {number} subjectId - The ID of the subject that contains the class.
 * @param {number} classId - The ID of the class to be deleted.
 * @returns {Promise<boolean>} - Returns true if the class was deleted successfully, otherwise throws an error.
 */
export const deleteClass = async (subjectId: number, classId: number): Promise<boolean> => {
    try {
        const response = await api.delete(
            `subjects/${subjectId}/classes/${classId}/delete`,
            { withCredentials: true }
        );
        return response.status === 204;
    } catch (error) {
        throw handleAxiosError(error);
    }
};