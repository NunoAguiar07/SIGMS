import {handleAxiosError} from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";


/**
 * Deletes a room with the specified ID.
 * @param {number} roomId - The ID of the room to be deleted.
 * @returns {Promise<boolean>} - Returns `true` if the room was deleted successfully, otherwise throws an error.
 */
export const deleteRoom = async (roomId: number): Promise<boolean> => {
    try {
        const response = await api.delete(
            `/rooms/${roomId}/delete`,
            { withCredentials: true }
        );
        return response.status === 204;
    } catch (error) {
        throw handleAxiosError(error);
    }
};