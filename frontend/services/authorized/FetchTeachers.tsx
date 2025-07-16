import api from "../interceptors/DeviceInterceptor";
import { handleAxiosError } from "../../utils/HandleAxiosError";
import {TeacherUser} from "../../types/teacher/TeacherUser";

/**
 * Fetches teachers from the API (no search support).
 *
 * @returns {Promise <TeacherUser[]>} A promise that resolves to an array of TeacherUser objects.
 * @throws {ParsedError} If the API request fails, an error will be thrown.
 */
// put a limit and offset


export const fetchTeachers = async (limit: number = 10, offset: number = 0): Promise<TeacherUser[]> => {
    try {
        const response = await api.get(`teachers?limit=${limit}&offset=${offset}`, { withCredentials: true });
        return response.status === 200 ? response.data.data : [];
    } catch (error) {
        throw handleAxiosError(error);
    }
};