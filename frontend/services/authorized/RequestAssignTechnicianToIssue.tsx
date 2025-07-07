import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";


/**
 * Requests to assign a technician to an issue by its ID.
 *
 * @param {number} issueId - The ID of the issue to which the technician will be assigned.
 * @returns {Promise<boolean>} A promise that resolves to true if the assignment was successful, false otherwise.
 * @throws {ParsedError} If the API request fails, an error will be thrown.
 */
export const requestAssignTechnicianToIssue = async (issueId: number): Promise<boolean> => {
    try {
        const response = await api.put(
            `/issue-reports/${encodeURIComponent(issueId)}/assign`,
            {},
            { withCredentials: true }
        );
        return response.status === 200;
    } catch (error) {
        throw handleAxiosError(error);
    }
};