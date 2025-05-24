import axios from 'axios';
import api from "../interceptors/DeviceInterceptor";
import {ErrorUriParser} from "../../Utils/UriParser";


/**
 * Fetches lectures from the server
 * @param setSchedule Callback to set schedule data in state
 * @param setError Callback to handle errors
 * @returns Promise with lectures or error
 */
export const getSchedule = (
    setSchedule: (data: any[]) => void,
    setError: (error: any) => void
) => {
    return async () => {
        try {
            const response = await api.get(`/schedule`, { withCredentials: true });
            setSchedule(response.data);
            console.log('Fetched lectures:', response.data);
        } catch (error) {
            console.log(error);
            if (axios.isAxiosError(error)) {
                if (error.response) {
                    const parsedError = {
                        status: error.response.status,
                        message: ErrorUriParser(error.response.data?.type) ||
                            error.response.data?.message ||
                            'Schedule request failed',
                    };
                    setError(parsedError);
                } else if (error.request) {
                    setError({ message: 'No response received from server' });
                } else {
                    setError({ message: error.message });
                }
            } else {
                setError({ message: 'An unexpected error occurred' });
            }
        }
    };
};
