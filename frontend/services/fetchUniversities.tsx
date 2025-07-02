import axios from "axios";
import {apiUrl} from "./fetchWelcome";
import {UniversityInterface} from "../types/UniversityInterface";
import {handleAxiosError} from "../utils/HandleAxiosError";



/**
 * Fetches a list of universities based on a search query.
 * @param {string} searchQuery - The search term to filter universities by name or description.
 * @returns {Promise<UniversityInterface[]>} - Returns a promise that resolves to an array of university objects.
 * @throws {Error} - Throws an error if the request fails.
 */
export const fetchUniversities = async (searchQuery: string): Promise<UniversityInterface[]> => {
    try {
        const response = await axios.get(
            `${apiUrl}universities?search=${encodeURIComponent(searchQuery)}`,
            {
                headers: {
                    'Content-Type': 'application/json',
                }
            }
        );
        return response.status === 200 ? response.data.data : [];
    } catch (error) {
        throw handleAxiosError(error);
    }
};