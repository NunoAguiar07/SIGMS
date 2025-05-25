import {ErrorUriParser} from "../../Utils/UriParser";
import axios from "axios";
import api from "../interceptors/DeviceInterceptor";

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
            console.log(error)
            if (axios.isAxiosError(error)) {
                if (error.response) {
                    const parsedError = {
                        status: error.response.status,
                        message: ErrorUriParser(error.response.data?.type) ||
                            error.response.data?.message ||
                            'Profile request failed'
                    };
                    setError(parsedError);
                } else if (error.request) {
                    setError({ message: "No response received from server" });
                } else {
                    setError({ message: error.message });
                }
            } else {
                setError({ message: 'An unexpected error occurred' });
            }
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
            if (axios.isAxiosError(error)) {
                if (error.response) {
                    const parsedError = {
                        status: error.response.status,
                        message: ErrorUriParser(error.response.data?.type) ||
                            error.response.data?.message ||
                            'Profile update failed'
                    };
                    setError(parsedError);
                } else if (error.request) {
                    setError({ message: "No response received from server" });
                } else {
                    setError({ message: error.message });
                }
            } else {
                setError({ message: 'An unexpected error occurred' });
            }
        }
    };
};