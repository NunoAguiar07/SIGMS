import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";
import {IssueReportInterface} from "../../interfaces/IssueReportInterface";


export const GetIssuesRequest = (
    limit: number,
    offset: number,
    unassigned: boolean,
    setIssues: (issues: IssueReportInterface[]) => void,
    setError: (error: any) => void
) => {
    return async () => {
        try {
            const response = await api.get(`/issue-reports?limit=${limit}&offset=${offset}&unassigned=${unassigned}`);
            if (response.status === 200) {
                console.log(response.data)
                return setIssues(response.data.data);
            }
            console.log(response.data)
        } catch (error) {
            handleAxiosError(error, setError);
        }
    }
};
