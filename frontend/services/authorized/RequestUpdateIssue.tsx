import {handleAxiosError} from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";

/**
 * Sends an update request for an issue report to the API.
 *
 * @param {number} issueId - The ID of the issue report to update.
 * @param {string} description - The updated description for the issue report.
 * @returns {Promise<boolean>} A promise that resolves to true if the update was successful, otherwise false.
 * @throws {ParsedError} If the API request fails, an error will be thrown.
 */
export const requestUpdateIssue = async (issueId: number, description: string): Promise<boolean> => {
    try {
        const response = await api.put(
            `issue-reports/${encodeURIComponent(issueId)}/update`,
            { description },
            { withCredentials: true }
        );
        return response.status === 200;
    } catch (error) {
        throw handleAxiosError(error);
    }
};