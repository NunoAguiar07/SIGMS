import {handleAxiosError} from "../../utils/HandleAxiosError";
import {Lecture} from "../../types/calendar/Lecture";
import api from "../interceptors/DeviceInterceptor";


/**
 * Fetches lectures by room ID
 * @param roomId - The room ID
 * @param limit - Number of items per page (default: 10)
 * @param offset - Number of items to skip (default: 0)
 * @returns Promise<Lecture[]>
 */
export const fetchLecturesByRoom = async (
    roomId: number,
    limit: number = 10,
    offset: number = 0
): Promise<Lecture[]> => {
    try {
        const response = await api.get(
            `/rooms/${roomId}/lectures`,
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