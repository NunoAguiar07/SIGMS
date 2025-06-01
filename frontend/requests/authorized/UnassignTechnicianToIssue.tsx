import { handleAxiosError } from "../../Utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";


export const UnassignTechnicianToIssueRequest = (
    issueId: number,
    setError: (error: any) => void
) => {
    return async () => {
        try {
            const response = await api.put(
                `issue-reports/${encodeURIComponent(issueId)}/unassign`,
                {},
                { withCredentials: true }
            );

           return response.status === 204
        } catch (error) {
            handleAxiosError(error, setError);
        }
    };
};