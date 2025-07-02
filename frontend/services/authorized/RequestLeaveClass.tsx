import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";


/**
 * Joins a class in a subject by sending a POST request to the API.
 *
 * @param {number} subjectId - The ID of the subject to which the class belongs.
 * @param {number} classId - The ID of the class to join.
 * @returns {Promise<boolean>} A promise that resolves to true if the class was joined successfully, false otherwise.
 * @throws {ParsedError} If the API request fails, an error will be thrown.
 */
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