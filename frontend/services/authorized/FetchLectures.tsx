import api from "../interceptors/DeviceInterceptor";
import {Lecture} from "../../types/calendar/Lecture";
import {handleAxiosError} from "../../utils/HandleAxiosError";

/**
 * Fetches all lectures from the server.
 * @returns {Promise<Lecture[]>} - Returns a promise that resolves to an array of lectures.
 * @throws {Error} - Throws an error if the request fails.
 */
export const fetchLectures = async () : Promise<Lecture[]> => {
    try {
        const response = await api.get(`/lectures`, { withCredentials: true });
        return response.data.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};