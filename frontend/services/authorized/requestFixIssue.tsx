import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";

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