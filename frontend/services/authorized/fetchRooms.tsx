import api from "../interceptors/DeviceInterceptor";
import {RoomInterface} from "../../types/RoomInterface";
import {handleAxiosError} from "../../utils/HandleAxiosError";


export const fetchRooms = async (
    searchQuery: string
): Promise<RoomInterface[]> => {
    try {
        const url = searchQuery
            ? `rooms?search=${encodeURIComponent(searchQuery)}`
            : 'rooms';
        const response = await api.get(url, { withCredentials: true });
        if (response.status === 200) {
            return response.data.data
        }
    } catch (error) {
        throw handleAxiosError(error);
    }
};

