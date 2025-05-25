import axios from "axios";


export const RegisterRequest = (email: string, username: string, password:string, role:string, universityId:number, setError: any) => {
    return async () => {
        try {
            console.log(email, username, password, role, universityId)
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
            console.log(error)
            setError(error)
        }
    }
};