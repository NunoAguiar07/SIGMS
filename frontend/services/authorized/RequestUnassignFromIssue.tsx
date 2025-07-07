import { handleAxiosError } from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";



/**
 * Unassigns the current user from an issue report by its ID.
 *
 * @param {number} issueId - The ID of the issue report to unassign from.
 * @returns {Promise<boolean>} A promise that resolves to true if the unassignment was successful, false otherwise.
 * @throws {ParsedError} If the API request fails, an error will be thrown.
 */
export const requestUnassignFromIssue = async (issueId: number): Promise<boolean> => {
    try {
        const response = await api.put(
            `issue-reports/${encodeURIComponent(issueId)}/unassign`,
            {},
            { withCredentials: true }
        );
        return response.status === 204;
    } catch (error) {
        throw handleAxiosError(error);
    }
};