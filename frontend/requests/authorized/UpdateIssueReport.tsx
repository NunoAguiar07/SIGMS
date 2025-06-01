import {handleAxiosError} from "../../Utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";


export const UpdateIssueReportRequest = (
    issueId: number,
    description: string,
    setError: (error: any) => void
) => {
    return async () => {
        try {
            const response = await api.put(
                `issue-reports/${encodeURIComponent(issueId)}/update`,
                { description },
                { withCredentials: true }
            );

            if (response.status === 200) {
                console.log('Issue updated:', response.data);
                return response.data;
            }
        } catch (error) {
            handleAxiosError(error, setError);
        }
    };
};