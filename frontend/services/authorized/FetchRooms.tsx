import api from "../interceptors/DeviceInterceptor";
import {RoomInterface} from "../../types/RoomInterface";
import {handleAxiosError} from "../../utils/HandleAxiosError";


/**
 * Fetches a list of rooms from the server, optionally filtered by a search query.
 * @param {string} searchQuery - The search term to filter rooms by name or description.
 * @param roomType - The type of room to filter by (e.g., 'classroom').
 * @returns {Promise<RoomInterface[]>} - Returns a promise that resolves to an array of room objects.
 * @throws {Error} - Throws an error if the request fails.
 */
export const fetchRooms = async (
    searchQuery: string,
    roomType?: string
): Promise<RoomInterface[]> => {
    try {
        const params = new URLSearchParams();

        if (searchQuery) {
            params.append('search', searchQuery);
        }

        if (roomType) {
            params.append('type', roomType);
        }

        const queryString = params.toString();
        const url = `rooms${queryString ? `?${queryString}` : ''}`;
        const response = await api.get(url, { withCredentials: true });
        return response.status === 200 ? response.data.data : [];
    } catch (error) {
        throw handleAxiosError(error);
    }
};

