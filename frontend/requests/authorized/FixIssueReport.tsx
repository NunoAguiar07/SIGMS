import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";


export const FixIssueReportRequest = (
    issueId: number,
    setError: (error: any) => void
) => {
    return async () => {
        try {
            const response = await api.delete(
                `/issue-reports/${encodeURIComponent(issueId)}/delete`,
                { withCredentials: true }
            );

            if (response.status === 204) {
                console.log('Issue fixed/deleted successfully');
                return true;
            } else return false
        } catch (error) {
            handleAxiosError(error, setError);
            return false
        }
    };
};