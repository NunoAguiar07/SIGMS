import {handleAxiosError} from "../../utils/HandleAxiosError";
import {apiUrl} from "../fetchWelcome";
import api from "../interceptors/DeviceInterceptor";



export const requestRegister = async (
    email: string,
    username: string,
    password: string,
    role: string,
    universityId: number
): Promise<string | undefined> => {
    try {
        const response = await api.post(
            `${apiUrl}auth/register`,
            { email, username, password, role, universityId },
            {
                headers: {
                    'Content-Type': 'application/json',
                },
            }

        );
        if (response.status === 201) {
            return response.data.token;
        }
        return undefined;

    } catch (error) {
        throw handleAxiosError(error);
    }
};