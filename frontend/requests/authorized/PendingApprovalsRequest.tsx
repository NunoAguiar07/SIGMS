import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";


export const getPendingApprovals = (limit: number, offset: number, setError: any) => {
    return async () => {
        try {
            const response = await api.get(
                `/assess-roles?limit=${limit}&offset=${offset}`,
                { withCredentials: true }
            );
            if (response.status === 200) {
                return response.data.data;
            }
        } catch (error) {
            handleAxiosError(error, setError)
        }
    };
};
