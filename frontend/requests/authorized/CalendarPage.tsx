import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";


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
            handleAxiosError(error, setError)
        }
    };
};
