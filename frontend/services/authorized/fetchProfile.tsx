import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import {ProfileInterface} from "../../types/ProfileInterface";

/**
 * Fetches the profile data of the currently logged-in user.
 * @returns {Promise<ProfileInterface>} - Returns a promise that resolves to the user's profile data.
 * @throws {Error} - Throws an error if the request fails.
 */
export const fetchProfile = async (): Promise<ProfileInterface> => {
    try {
        const response = await api.get('/profile', { withCredentials: true });
        return response.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};