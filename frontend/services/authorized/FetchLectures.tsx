import api from "../interceptors/DeviceInterceptor";
import {Lecture} from "../../types/calendar/Lecture";
import {handleAxiosError} from "../../utils/HandleAxiosError";

/**
 * Fetches all lectures from the server.
 * @returns {Promise<Lecture[]>} - Returns a promise that resolves to an array of lectures.
 * @throws {Error} - Throws an error if the request fails.
 */
export const fetchLectures = async (
    limit: number = 10,
    offset: number = 0
): Promise<Lecture[]> => {
    try {
        const response = await api.get(
            `/lectures`,
            {
                params: { limit, offset },
                withCredentials: true
            }
        );
        return response.data.data || response.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};