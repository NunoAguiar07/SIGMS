import {handleAxiosError} from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";

/**
 * Creates a new issue report for a specific room.
 * @param {number} roomId - The ID of the room where the issue is reported.
 * @param {string} description - A detailed description of the issue.
 * @returns {Promise<boolean>} - Returns true if the issue report was created successfully, otherwise throws an error.
 */
export const CreateIssueReportRequest = async (
    roomId: number,
    description: string
): Promise<boolean> => {
    try {
        const response = await api.post(
            `rooms/${encodeURIComponent(roomId)}/issues/add`,
            { description },
            { withCredentials: true }
        );
        return response.status === 201
    } catch (error) {
        throw handleAxiosError(error);
    }
};