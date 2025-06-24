import {handleAxiosError} from "../../utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";
import {IssueReportInterface} from "../../types/IssueReportInterface";

export const fetchIssueReportsByRoom = async (roomId: number): Promise<IssueReportInterface[]> => {
    try {
        const response = await api.get(`rooms/${encodeURIComponent(roomId)}/issues`, {
            withCredentials: true
        });
        console.log("Fetched issue reports for room:", response.data);
        return response.data.data;
    } catch (error) {
        throw handleAxiosError(error);
    }
}