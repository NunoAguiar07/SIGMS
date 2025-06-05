import axios from "axios";
import {apiUrl} from "./fetchWelcome";
import {UniversityInterface} from "../types/UniversityInterface";
import {handleAxiosError} from "../utils/HandleAxiosError";

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