import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";


/**
 * Requests to fix an issue by deleting the issue report.
 *
 * @param {number} issueId - The ID of the issue to be fixed.
 * @returns {Promise<boolean>} A promise that resolves to true if the request was successful, false otherwise.
 * @throws {ParsedError} If the API request fails, an error will be thrown.
 */
export const requestFixIssue = async (issueId: number): Promise<boolean> => {
    try {
        const response = await api.delete(
            `issue-reports/${encodeURIComponent(issueId)}/delete`,
            { withCredentials: true }
        );
        return response.status === 204;
    } catch (error) {
        throw handleAxiosError(error);
    }
};