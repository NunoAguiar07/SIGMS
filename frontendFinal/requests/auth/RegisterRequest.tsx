import axios from "axios";


export const RegisterRequest = (email: string, username: string, password:string, role:string, universityId:number, setError: any) => {
    return async () => {
        try {
            const response = await axios.post(
                `${process.env.EXPO_PUBLIC_API_URL}/auth/register`,
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
            setError(error)
        }
    }
};