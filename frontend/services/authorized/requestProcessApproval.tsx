import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";


export const requestProcessApproval = async (token: string, approve: boolean): Promise<boolean> => {
    try {
        const response = await api.put(
            `assess-roles/validate?token=${token}&status=${approve ? 'APPROVED' : 'REJECTED'}`,
            {},
            { withCredentials: true }
        );
        return response.status === 204;
    } catch (error) {
        throw handleAxiosError(error);
    }
};