import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import {RoomType} from "../../types/welcome/FormCreateEntityValues";


/**
 * Creates a new room with the specified name, capacity, and type.
 * @param {string} roomName - The name of the room to be created.
 * @param {number} capacity - The maximum number of people that the room can accommodate.
 * @param {RoomType} roomType - The type of the room (e.g., LECTURE_HALL, SEMINAR_ROOM).
 * @returns {Promise<boolean>} - Returns true if the room was created successfully, otherwise throws an error.
 */
export const createRoom = async (
    roomName: string,
    capacity: number,
    roomType: RoomType
): Promise<boolean> => {
    try {
        const response = await api.post('/rooms/add', {
            name: roomName,
            capacity: capacity,
            type: roomType
        }, { withCredentials: true });
        return response.status === 201;
    } catch (error) {
        throw handleAxiosError(error);
    }
};