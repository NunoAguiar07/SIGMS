import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import {AccessRoleInterface} from "../../types/AccessRoleInterface";


/**
 * Fetches pending approvals for access roles.
 * @param {number} limit - The maximum number of access roles to fetch.
 * @param {number} offset - The offset for pagination.
 * @returns {Promise<AccessRoleInterface[]>} - Returns a promise that resolves to an array of access roles.
 * @throws {Error} - Throws an error if the request fails.
 */
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
