import {IssueReportInterface} from "../../interfaces/IssueReportInterface";
import {handleAxiosError} from "../../Utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";


export const GetAssignedIssuesRequest = (
    limit: number,
    offset: number,
    setIssues: (issues: IssueReportInterface[]) => void,
    setError: (error: any) => void
) => {
    return async () => {
        try {
            const response = await api.get(`issue-reports/me?limit=${limit}&offset=${offset}`);
            if (response.status === 200) {
                return setIssues(response.data.data);
            }
        } catch (error) {
            handleAxiosError(error, setError);
        }
    }
};