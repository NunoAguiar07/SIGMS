import {Lecture} from "../../types/calendar/Lecture";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";


/**
 * Fetches lectures by class (subject and class ID)
 * @param subjectId - The subject ID
 * @param classId - The class ID
 * @param limit - Number of items per page (default: 10)
 * @param offset - Number of items to skip (default: 0)
 * @returns Promise<Lecture[]>
 */
export const fetchLecturesByClass = async (
    subjectId: number,
    classId: number,
    limit: number = 10,
    offset: number = 0
): Promise<Lecture[]> => {
    try {
        const response = await api.get(
            `/subjects/${subjectId}/classes/${classId}/lectures`,
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
