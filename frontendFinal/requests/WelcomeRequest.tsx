import axios from "axios";
import {handleAxiosError} from "../Utils/HandleAxiosError";

export const apiUrl = 'http://localhost:8080/api'

/**
 * Const "GetData" represents the information we want to represent on the home page.
 * @param setWelcome represents the information to represent in the home page.
 * @param setError represents the error if happens in the screen.
 * @return the information of the home provided by the server.
 */
export const WelcomeRequest = (setWelcome: (data: any) => void, setError: (error: any) => void) => {
    return async () => {
        try {
            const response = await axios.get(apiUrl);
            setWelcome(response.data);
        } catch (error) {
            handleAxiosError(error, setError);
        }
    }
}