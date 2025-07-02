import {SchoolClassInterface} from "../../types/SchoolClassInterface";
import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";


/**
 * Fetches the classes associated with the currently logged-in user from the API.
 *
 * @returns {Promise<SchoolClassInterface[]>} A promise that resolves to an array of school classes.
 * @throws {ParsedError} If the API request fails, an error will be thrown.
 */
export const fetchUserClasses = async (
): Promise<SchoolClassInterface[]> => {
    try {
        const response = await api.get(
             '/userClasses',
            {
                withCredentials: true
            }
        );
        console.log(response.data);
        return response.data.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};