import axios from "axios";
import {handleAxiosError} from "../../Utils/HandleAxiosError";


export const VerifyAccountRequest = (token: string, setError: any) => {
    return async () => {
        const apiUrl = process.env.EXPO_PUBLIC_API_URL
        try {
            const response = await axios.get(
                `${apiUrl}/auth/verify-account?token=${token}`,
            );
            return response.status === 204;
        } catch (error) {
            handleAxiosError(error, setError)
            return false;
        }
    };
};