import axios from "axios";
import {handleAxiosError} from "../../Utils/HandleAxiosError";
import {apiUrl} from "../fetchWelcome";


export const requestVerifyAccount = async (token: string): Promise<boolean> => {
    try {
        const response = await axios.get(
            `${apiUrl}auth/verify-account?token=${token}`
        );
        return response.status === 204;
    } catch (error) {
        throw handleAxiosError(error);
    }
};