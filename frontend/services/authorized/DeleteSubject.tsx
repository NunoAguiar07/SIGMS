import {handleAxiosError} from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";


/**
 * Deletes a subject by its ID.
 * @param {number} subjectId - The ID of the subject to delete.
 * @returns {Promise<boolean>} A promise that resolves to true if the deletion was successful, false otherwise.
 */
export const deleteSubject = async (subjectId: number): Promise<boolean> => {
    try {
        const response = await api.delete(
            `/subjects/${subjectId}/delete`,
        { withCredentials: true }
        );
        return response.status === 204;
    } catch (error) {
        throw handleAxiosError(error);
    }
}