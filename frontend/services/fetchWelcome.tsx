import axios from "axios";
import {handleAxiosError} from "../utils/HandleAxiosError";
import {WelcomeData} from "../types/welcome/WelcomeInterfaces";
import {isMobile} from "../utils/DeviceType";



export const apiUrl: string =  ((): string => {
    if(__DEV__){
        return !isMobile
            ? 'https://localhost/api/'
            : 'http://10.0.2.2:8080/api/';
    } else {
        return process.env.EXPO_PUBLIC_API_URL
    }
})()
/**
 * Fetches welcome data from the server.
 * This function constructs the API URL based on the device type (mobile or web)
 * and retrieves welcome data from the specified endpoint.
 *
 * @returns {Promise<WelcomeData>} - A promise that resolves to the welcome data.
 * @throws {Error} - Throws an error if the request fails.
 */

export const fetchWelcome = async (): Promise<WelcomeData> => {
    try {
        const response = await axios.get(apiUrl);
        return response.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};