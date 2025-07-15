import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";


/**
 * Deletes a lecture with the specified ID.
 * @param {number} lectureId - The ID of the lecture to be deleted.
 * @returns {Promise<boolean>} - Returns `true` if the lecture was deleted successfully, otherwise throws an error.
 */
export const deleteLecture = async (lectureId: number): Promise<boolean> => {
    try {
        const response = await api.delete(
            `/lectures/${lectureId}/delete`,
            { withCredentials: true }
        );
        return response.status === 204;
    } catch (error) {
        throw handleAxiosError(error);
    }
};