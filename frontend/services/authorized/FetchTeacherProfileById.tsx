import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import {TeacherUser} from "../../types/teacher/TeacherUser";


/**
 * Fetches the profile of a teacher by their ID from the API.
 *
 * @param {number} teacherId - The ID of the teacher whose profile is to be fetched.
 * @returns {Promise<any>} A promise that resolves to the teacher's profile data.
 * @throws {ParsedError} If the API request fails, an error will be thrown.
 */
export const fetchTeacherProfileById = async (teacherId: number): Promise<TeacherUser> => {
    try {
        const response = await api.get(`/profile/${encodeURIComponent(teacherId)}`, { withCredentials: true });
        return response.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
}