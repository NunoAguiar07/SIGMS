import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";
import {IssueReportInterface} from "../../types/IssueReportInterface";


export const fetchUnassignedIssues = async (
    limit: number,
    offset: number
): Promise<IssueReportInterface[]> => {
    try {
        const response = await api.get(`/issue-reports?limit=${limit}&offset=${offset}&unassigned=true`);
        return response.data.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};
