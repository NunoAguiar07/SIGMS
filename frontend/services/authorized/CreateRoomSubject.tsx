import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import {RoomType} from "../../types/welcome/FormCreateEntityValues";


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