import axios from "axios";
import {handleAxiosError} from "../../Utils/HandleAxiosError";
import {apiUrl} from "../WelcomeRequest";


export const VerifyAccountRequest = (token: string, setError: any) => {
    return async () => {
        try {
            const response = await axios.get(
                `${apiUrl}auth/verify-account?token=${token}`,
            );
            return response.status === 204;
        } catch (error) {
            handleAxiosError(error, setError)
            return false;
        }
    };
};