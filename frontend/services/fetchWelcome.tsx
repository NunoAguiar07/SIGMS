import axios from "axios";
import {handleAxiosError} from "../utils/HandleAxiosError";
import {WelcomeData} from "../types/welcome/WelcomeInterfaces";
import {isMobile} from "../utils/DeviceType";

export const apiUrl = !isMobile
    ? 'https://localhost/api/'
    : 'http://10.0.2.2:8080/api/';

export const fetchWelcome = async (): Promise<WelcomeData> => {
    try {
        const response = await axios.get(apiUrl);
        return response.data;
    } catch (error) {
        console.log(error);
        throw handleAxiosError(error);
    }
};