import {ErrorUriParser} from "../Utils/UriParser";
import axios from "axios";

export const apiUrl = 'http://localhost:8080/api'

/**
 * Const "GetData" represents the information we want to represent on the home page.
 * @param setWelcome represents the information to represent in the home page.
 * @param setError represents the error if happens in the screen.
 * @return the information of the home provided by the server.
 */
export const GetData = (setWelcome: (data: any) => void, setError: (error: any) => void) => {
    return async () => {
        try {
            const response = await axios.get(apiUrl);
            setWelcome(response.data);
        } catch (error) {
            if (axios.isAxiosError(error)) {
                if (error.response) {
                    const parsedError = {
                        status: error.response.status,
                        message: ErrorUriParser(error.response.data?.type)
                    };
                    setError(parsedError);
                } else if (error.request) {
                    setError({ message: "No response received from server" });
                } else {
                    setError({ message: error.message });
                }
            } else {
                setError(error);
            }
        }
    }
}