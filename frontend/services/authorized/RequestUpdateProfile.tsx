import {ProfileInterface} from "../../types/ProfileInterface";
import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";


/**
 * Updates the user's profile with the provided data.
 *
 * @param {Partial<ProfileInterface>} profileData - The profile data to update.
 * @returns {Promise<ProfileInterface>} A promise that resolves to the updated profile.
 * @throws {ParsedError} If the API request fails, an error will be thrown.
 */
export const requestUpdateProfile = async (profileData: Partial<ProfileInterface>): Promise<ProfileInterface> => {
    try {
        const response = await api.put('/profile/update', profileData, { withCredentials: true });
        return response.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};