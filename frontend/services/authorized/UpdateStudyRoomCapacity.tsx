import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";



/**
 * Updates the study room capacity by sending a request to the API.
 *
 * @returns {Promise<void>} A promise that resolves when the update is successful.
 * @throws {ParsedError} If the API request fails, an error will be thrown.
 */
export const updateStudyRoomCapacity = async (): Promise<void> => {
    try {
        await api.put('/devices/update', { withCredentials: true });
    } catch (error) {
        throw handleAxiosError(error);
    }
};