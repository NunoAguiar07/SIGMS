import axios from "axios";
import {handleAxiosError} from "../../Utils/HandleAxiosError";


export const VerifyAccountRequest = (token: string, setError: any) => {
    return async () => {
        try {
            const response = await axios.get(
                `${process.env.EXPO_PUBLIC_API_URL}/auth/verify-account?token=${token}`,
            );
            return response.status === 204;
        } catch (error) {
            handleAxiosError(error, setError)
            return false;
        }
    };
};