import {handleAxiosError} from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";
import {IssueReportInterface} from "../../types/IssueReportInterface";

/**
 * Fetches all issue reports for a specific room.
 * @param {number} roomId - The ID of the room for which to fetch issue reports.
 * @returns {Promise<IssueReportInterface[]>} - Returns a promise that resolves to an array of issue reports.
 */
export const fetchIssueReportsByRoom = async (roomId: number): Promise<IssueReportInterface[]> => {
    try {
        const response = await api.get(`rooms/${encodeURIComponent(roomId)}/issues`, {
            withCredentials: true
        });
        return response.data.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
}