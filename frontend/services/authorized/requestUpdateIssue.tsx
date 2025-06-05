import {handleAxiosError} from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";

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