import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import {IssueReportInterface} from "../../types/IssueReportInterface";



/**
 * Fetches unassigned issues from the API with pagination.
 *
 * @param {number} limit - The maximum number of issues to fetch.
 * @param {number} offset - The offset for pagination.
 * @returns {Promise<IssueReportInterface[]>} A promise that resolves to an array of unassigned issue reports.
 * @throws {ParsedError} If the API request fails, an error will be thrown.
 */
export const fetchUnassignedIssues = async (
    limit: number,
    offset: number
): Promise<IssueReportInterface[]> => {
    try {
        const response = await api.get(`/issue-reports?limit=${limit}&offset=${offset}&unassigned=true`);
        return response.data.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};
