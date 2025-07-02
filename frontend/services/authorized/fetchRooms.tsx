import api from "../interceptors/DeviceInterceptor";
import {RoomInterface} from "../../types/RoomInterface";
import {handleAxiosError} from "../../utils/HandleAxiosError";


/**
 * Fetches a list of rooms from the server, optionally filtered by a search query.
 * @param {string} searchQuery - The search term to filter rooms by name or description.
 * @returns {Promise<RoomInterface[]>} - Returns a promise that resolves to an array of room objects.
 * @throws {Error} - Throws an error if the request fails.
 */
export const fetchRooms = async (
    searchQuery: string
): Promise<RoomInterface[]> => {
    try {
        const url = searchQuery
            ? `rooms?search=${encodeURIComponent(searchQuery)}`
            : 'rooms';
        const response = await api.get(url, { withCredentials: true });
        return response.status === 200 ? response.data.data : [];
    } catch (error) {
        throw handleAxiosError(error);
    }
};

