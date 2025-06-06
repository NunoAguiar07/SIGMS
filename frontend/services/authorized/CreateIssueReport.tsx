import {handleAxiosError} from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";

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