import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import {SubjectInterface} from "../../types/SubjectInterface";



/**
 * Fetches subjects from the API based on an optional search query.
 *
 * @param {string} searchQuery - The search term to filter subjects (optional).
 * @returns {Promise<SubjectInterface[]>} A promise that resolves to an array of subjects.
 * @throws {ParsedError} If the API request fails, an error will be thrown.
 */
export const fetchSubjects = async (searchQuery: string): Promise<SubjectInterface[]> => {
    try {
        const url = searchQuery
            ? `subjects?search=${encodeURIComponent(searchQuery)}`
            : 'subjects';
        const response = await api.get(url, { withCredentials: true });
        return response.status === 200 ? response.data.data : [];
    } catch (error) {
        throw handleAxiosError(error);
    }
};