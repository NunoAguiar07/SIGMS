import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";


export const processApproval = (token: string, approve: boolean, setError: any) => {
    return async () => {
        try {
            const response = await api.put(
                `assess-roles/validate?token=${token}&status=${approve ? 'APPROVED' : 'REJECTED'}`,
                {},
                { withCredentials: true }
            );
            return response.status === 204;
        } catch (error) {
            handleAxiosError(error, setError)
        }
    };
};