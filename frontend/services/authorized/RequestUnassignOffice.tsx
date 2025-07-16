import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";


/**
 * Unassigns a teacher from an office room.
 * @param roomId
 * @param teacherId
 */
export const unassignTeacherFromOffice = async (roomId: number, teacherId: number): Promise<boolean> => {
    try {
        const response = await api.delete(
            `/rooms/offices/${roomId}/teachers/remove`,
            {
                data: {teacherId},
                withCredentials: true
            }
        );
        return response.status === 204;
    } catch (error) {
        throw handleAxiosError(error);
    }
}