import { handleAxiosError } from "../../Utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";


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