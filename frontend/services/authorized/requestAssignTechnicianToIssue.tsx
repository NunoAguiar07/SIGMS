import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";

export const requestAssignTechnicianToIssue = async (issueId: number): Promise<boolean> => {
    try {
        const response = await api.put(
            `/issue-reports/${encodeURIComponent(issueId)}/assign`,
            {},
            { withCredentials: true }
        );
        return response.status === 200;
    } catch (error) {
        throw handleAxiosError(error);
    }
};