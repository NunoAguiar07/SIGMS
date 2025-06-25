import axios from "axios";
import {handleAxiosError} from "../utils/HandleAxiosError";
import {WelcomeData} from "../types/welcome/WelcomeInterfaces";

export const apiUrl = 'https://localhost/api/';

export const fetchWelcome = async (): Promise<WelcomeData> => {
    try {
        const response = await axios.get(apiUrl);
        return response.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};