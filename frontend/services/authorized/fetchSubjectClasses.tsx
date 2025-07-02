import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import {SchoolClassInterface} from "../../types/SchoolClassInterface";



/**
 * Fetches the classes associated with a specific subject from the API.
 *
 * @param {number} subjectId - The ID of the subject for which to fetch classes.
 * @returns {Promise<SchoolClassInterface[]>} A promise that resolves to an array of school classes.
 * @throws {ParsedError} If the API request fails, an error will be thrown.
 */
export const fetchSubjectClasses = async (subjectId: number): Promise<SchoolClassInterface[]> => {
    try {
        const response = await api.get(`/subjects/${encodeURIComponent(subjectId)}/classes`, { withCredentials: true });
        return response.data.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};