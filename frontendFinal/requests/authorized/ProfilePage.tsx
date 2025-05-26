import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";

export const apiUrl = 'http://localhost:8080/api';

/**
 * Fetches user profile data from the server
 * @param setProfile Callback to set profile data in state
 * @param setError Callback to handle errors
 * @returns Promise with profile data or error
 */
export const GetProfile = (
    setProfile: (data: any) => void,
    setError: (error: any) => void,
) => {
    return async () => {
        try {
            const response = await api.get(`/profile`, { withCredentials: true });
            setProfile(response.data)
        } catch (error) {
            handleAxiosError(error, setError)
        }
    };
};



export const UpdateProfile = (
    profileData: any,
    setProfile: (data: any) => void,
    setError: (error: any) => void,
    authToken: string
) => {
    return async () => {
        try {
            const response = await api.put(`/profile`, profileData, { withCredentials: true });
            setProfile(response.data);
        } catch (error) {
            handleAxiosError(error, setError)
        }
    };
};