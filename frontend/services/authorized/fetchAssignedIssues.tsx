import {IssueReportInterface} from "../../types/IssueReportInterface";
import {handleAxiosError} from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";


/**
 * Fetches a paginated list of issue reports assigned to the current user.
 * @param {number} limit - The maximum number of issue reports to fetch.
 * @param {number} offset - The offset for pagination.
 * @returns {Promise<IssueReportInterface[]>} - Returns a promise that resolves to an array of issue reports.
 */
export const fetchAssignedIssues = async (limit: number, offset: number): Promise<IssueReportInterface[]> => {
    try {
        const response = await api.get(`issue-reports/me?limit=${limit}&offset=${offset}`, {
            withCredentials: true
        });
        return response.data.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};