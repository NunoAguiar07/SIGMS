import api from "../interceptors/DeviceInterceptor";
import { RoomInterface } from "../../types/RoomInterface";
import { handleAxiosError } from "../../utils/HandleAxiosError";

export const fetchRooms = async (
    searchQuery: string = "",
    roomType?: string,
    limit?: number,
    offset?: number
): Promise<RoomInterface[]> => {
    try {
        const params = new URLSearchParams();

        if (searchQuery) {
            params.append("search", searchQuery);
        }
        if (roomType) {
            params.append("type", roomType);
        }
        if (limit !== undefined) {
            params.append("limit", limit.toString());
        }
        if (offset !== undefined) {
            params.append("offset", offset.toString());
        }

        const queryString = params.toString();
        const url = `rooms${queryString ? `?${queryString}` : ""}`;
        const response = await api.get(url, { withCredentials: true });
        return response.status === 200 ? response.data.data : [];
    } catch (error) {
        throw handleAxiosError(error);
    }
};
