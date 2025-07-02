import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";



/**
 * Joins a class by sending a request to the API.
 *
 * @param {number} subjectId - The ID of the subject to which the class belongs.
 * @param {number} classId - The ID of the class to join.
 * @returns {Promise<boolean>} A promise that resolves to true if the class was successfully joined, false otherwise.
 * @throws {ParsedError} If the API request fails, an error will be thrown.
 */
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