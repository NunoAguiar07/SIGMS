import api from "../interceptors/DeviceInterceptor";
import {RoomInterface} from "../../interfaces/RoomInterface";
import {ErrorInterface} from "../../interfaces/ErrorInterface";

export const getRooms = (
    searchQuery: string,
    setRooms: (rooms: RoomInterface[]) => void,
    setError: (error: ErrorInterface) => void
) => {
    return async () => {
        try {
            const url = searchQuery
                ? `rooms?search=${encodeURIComponent(searchQuery)}`
                : 'rooms';
            const response = await api.get(url, { withCredentials: true });

            if (response.status === 200) {
                setRooms(response.data.data);
            }
        } catch (error: any) {
            setError({
                status: error?.response?.status || 500,
                message: error?.message || 'Failed to fetch rooms'
            });
        }
    };
};

