import axios from "axios";
import {handleAxiosError} from "../../Utils/HandleAxiosError";


export const RegisterRequest = (email: string, username: string, password:string, role:string, universityId:number, setError: any) => {
    return async () => {
        const apiUrl = process.env.EXPO_PUBLIC_API_URL
        try {
            console.log(email, username, password, role, universityId)
            const response = await axios.post(
                `${apiUrl}/auth/register`,
                {email, username, password, role, universityId},
                {
                    headers: {
                        'Content-Type': 'application/json',
                    }
                }
            );
            if(response.status === 201) {
                return response.data.message;
            }
        } catch (error) {
            handleAxiosError(error, setError)
        }
    }
};