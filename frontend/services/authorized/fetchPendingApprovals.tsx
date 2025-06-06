import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import {AccessRoleInterface} from "../../types/AccessRoleInterface";


export const fetchPendingApprovals = async (limit: number, offset: number): Promise<AccessRoleInterface[]> => {
    try{
        const response = await api.get(`assess-roles?limit=${limit}&offset=${offset}`, {
            withCredentials: true
        });
        return response.data.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};
