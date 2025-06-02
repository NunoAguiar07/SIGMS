import {IssueReportInterface} from "../../types/IssueReportInterface";
import {handleAxiosError} from "../../Utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";


export const fetchAssignedIssues = async (limit: number, offset: number): Promise<IssueReportInterface[]> => {
    try {
        const response = await api.get(`issue-reports/me?limit=${limit}&offset=${offset}`, {
            withCredentials: true
        });
        return response.data.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
};