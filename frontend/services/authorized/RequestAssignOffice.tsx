import { handleAxiosError } from '../../utils/HandleAxiosError';
import api from "../interceptors/DeviceInterceptor";

/**
 * Assigns a teacher to an office room.
 * @param roomId
 * @param teacherId
 */
export const assignTeacherToOffice = async (roomId: number, teacherId: number): Promise<boolean> => {
    try {
        const response = await api.put(
            `/rooms/offices/${roomId}/teachers/add`,
            { teacherId },
            { withCredentials: true }
        );
        return response.status === 200;
    } catch (error) {
        throw handleAxiosError(error);
    }
};