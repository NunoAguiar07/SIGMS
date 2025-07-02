import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";


/**
 * Requests approval for a process based on the provided token and approval status.
 *
 * @param {string} token - The token associated with the process.
 * @param {boolean} approve - Indicates whether to approve or reject the process.
 * @returns {Promise<boolean>} A promise that resolves to true if the request was successful, otherwise false.
 * @throws {ParsedError} If the API request fails, an error will be thrown.
 */
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