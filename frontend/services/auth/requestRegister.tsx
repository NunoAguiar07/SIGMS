import axios from "axios";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import {apiUrl} from "../fetchWelcome";


export const requestRegister = async (
    email: string,
    username: string,
    password: string,
    role: string,
    universityId: number
): Promise<string | undefined> => {
    try {
        const response = await axios.post(
            `${apiUrl}auth/register`,
            { email, username, password, role, universityId },
            {
                headers: {
                    'Content-Type': 'application/json',
                }
            }
        );
        return response.status === 201 ? response.data.message : undefined;
    } catch (error) {
        throw handleAxiosError(error);
    }
};